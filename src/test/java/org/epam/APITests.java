package org.epam;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.epam.dto.Playlist;
import org.epam.dto.Track;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class APITests {
    private static final String BASE_URL_PLAYLISTS = "http://localhost:8888/api/playlists";
    private static final String BASE_URL_TRACKS = "http://localhost:8888/api/tracks";

    private RequestSpecification requestSpecification;

    @BeforeMethod
    public void authSetUp() {
        requestSpecification = RestAssured.given().auth().oauth2("8d3edc50fd5dbb75c78aa0e6b003827314f21f4aa8f03facd79465c96ce44c55");
        setCommonParams(requestSpecification);
    }

    @Test
    public void createPlaylistTest() {
        Playlist expectedPlaylist = createPlaylist();
        Response response = requestSpecification.body(expectedPlaylist).expect().statusCode(HttpStatus.SC_CREATED).log().ifError()
                .when().post(BASE_URL_PLAYLISTS);
        System.out.println(response.asPrettyString());
        Playlist createdPlaylist = response.as(Playlist.class);
        Assert.assertEquals(createdPlaylist.getName(), expectedPlaylist.getName(),
                "Expected user doesn't have the correct name");
    }

    @Test
    public void updatePlaylistTest() {
        Playlist playlistToUpdate = createPlaylist();
        Response response = requestSpecification.body(playlistToUpdate).expect().statusCode(HttpStatus.SC_CREATED).log().ifError()
                .when().post(BASE_URL_PLAYLISTS);
        Playlist createdPlaylist = response.as(Playlist.class);
        Playlist newPlaylist = updatePlaylist();
        Response newResponse = requestSpecification.body(newPlaylist).expect().statusCode(HttpStatus.SC_OK)
                .when().put(BASE_URL_PLAYLISTS + "/" + createdPlaylist.getId());
        Playlist updatedPlaylist = newResponse.as(Playlist.class);
        Assert.assertEquals(updatedPlaylist.getName(), newPlaylist.getName(),
                "Updated user doesn't have the correct name");
        Assert.assertEquals(updatedPlaylist.getDescription(), newPlaylist.getDescription(),
                "Updated user doesn't have the correct description");
        Assert.assertEquals(updatedPlaylist.getPublic(), newPlaylist.getPublic(),
                "Updated user doesn't have the correct public info");
    }
    @Test
    public void addTracksToPlaylistTest() {

        //create playlist
        Playlist expectedPlaylist = createPlaylist();
        Response response = requestSpecification.body(expectedPlaylist).expect().statusCode(HttpStatus.SC_CREATED)
                .when().post(BASE_URL_PLAYLISTS);
        Playlist createdPlaylist = response.as(Playlist.class);
        //post track by trackId
        Playlist trackToAdd = getTrackByIdFirst();
        int trackId = trackToAdd.getTrackId();
        Response postResponse = requestSpecification.body(trackToAdd).expect().statusCode(HttpStatus.SC_OK)
                .when().post(BASE_URL_PLAYLISTS + "/" + createdPlaylist.getId() + "/tracks/add");
        //get info about track
        Response getTrackResponse = requestSpecification.expect().statusCode(HttpStatus.SC_OK)
                .when().get(BASE_URL_TRACKS + "/" + trackId);
        String receivedTrack = getTrackResponse.asPrettyString();
        //get playlist by id
        Response getPlaylistResponse = requestSpecification.expect().statusCode(HttpStatus.SC_OK)
                .when().get(BASE_URL_PLAYLISTS + "/" + createdPlaylist.getId());
        Playlist receivedPlaylist = getPlaylistResponse.as(Playlist.class);
        //get all tracks added to the playlist
        Response getTracksResponse = (Response) requestSpecification.expect().statusCode(HttpStatus.SC_OK)
                .when().get(BASE_URL_PLAYLISTS + "/" + createdPlaylist.getId() + "/tracks");
              //  .then().assertThat()
              //  .body("id", equalTo(trackId));
        System.out.println(getTracksResponse.asPrettyString());

        List<Track> tracks = getTracksResponse.jsonPath().getList("", Track.class);
        Assert.assertTrue(tracks.contains(getTrackResponse),
                "Expected tracks list should contain the track with info " + receivedTrack);
    }

    @Test
    public void removeTrackFromPlaylist() {
        int trackIdToCheck = 2;
        //create playlist
        Playlist expectedPlaylist = createPlaylist();
        Response response = requestSpecification.body(expectedPlaylist).expect().statusCode(HttpStatus.SC_CREATED).log().ifError()
                .when().post(BASE_URL_PLAYLISTS);
        Playlist createdPlaylist = response.as(Playlist.class);
        Assert.assertEquals(createdPlaylist.getName(), expectedPlaylist.getName(),
                "Expected user doesn't have the correct name");
        //add first track
        Playlist trackToAddFirst = getTrackByIdFirst();
        Response postTrackFirstResponse = requestSpecification.body(trackToAddFirst).expect().statusCode(HttpStatus.SC_OK)
                .when().post(BASE_URL_PLAYLISTS + "/" + createdPlaylist.getId() + "/tracks/add");
        //add second track
        Playlist trackToAddSecond = getTrackByIdSecond();
        Response postTrackSecondResponse = requestSpecification.body(trackToAddSecond).expect().statusCode(HttpStatus.SC_OK)
                .when().post(BASE_URL_PLAYLISTS + "/" + createdPlaylist.getId() + "/tracks/add");
        //remove second track from the playlist
        Response removeTrackResponse = requestSpecification.body(trackToAddSecond).expect().statusCode(HttpStatus.SC_OK)
                .when().delete(BASE_URL_PLAYLISTS + "/" + createdPlaylist.getId() + "/tracks/remove");
        //get info about track
        Response getTrack = requestSpecification.expect().statusCode(HttpStatus.SC_OK)
                .when().get(BASE_URL_TRACKS + "/" + trackIdToCheck);
        Track recievedTrack = getTrack.as(Track.class);
        //get all tracks added to the playlist
        Response getTracksResponse = (Response) requestSpecification.expect().statusCode(HttpStatus.SC_OK)
                .when().get(BASE_URL_PLAYLISTS + "/" + createdPlaylist.getId() + "/tracks");

        List<Track> tracks = getTracksResponse.jsonPath().getList("", Track.class);
        Assert.assertFalse(tracks.contains(recievedTrack),
                "Expected tracks list should contain the track with id " + trackIdToCheck);
    }

    @Test
    public void removePlaylist() {
        //create playlist
        Playlist playlistToDelete = createPlaylist();
        Response response = requestSpecification.body(playlistToDelete).expect().statusCode(HttpStatus.SC_CREATED)
                .when().post(BASE_URL_PLAYLISTS);
        Playlist createdPlaylist = response.as(Playlist.class);
        //delete playlist
        requestSpecification.expect().statusCode(HttpStatus.SC_OK)
                .when().delete(BASE_URL_PLAYLISTS + "/" + createdPlaylist.getId());
        //check that deleted playlist is not present in list of all playlists
        Response responseAllPlaylists = (Response) requestSpecification.expect().statusCode(HttpStatus.SC_OK)
                .when().get(BASE_URL_PLAYLISTS);
        List<Playlist> playlists = responseAllPlaylists.jsonPath().getList("", Playlist.class);
        Assert.assertFalse(playlists.contains(playlistToDelete),
                "Expected users list should contain the users with id " + createdPlaylist.getId());
    }

    private void setCommonParams(RequestSpecification requestSpecification) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        requestSpecification.headers(headers);
    }

    private Playlist createPlaylist() {
        Random random = new Random();
        Playlist playlist = new Playlist();
        playlist.setDescription("descripion" + random.nextInt());
        playlist.setPublic(true);
        playlist.setName("test" + random.nextInt());
        playlist.setUserId(1);
        return playlist;
    }

    private Playlist updatePlaylist() {
        Random random = new Random();
        Playlist playlist = new Playlist();
        playlist.setDescription("descripion updated" + random.nextInt());
        playlist.setPublic(true);
        playlist.setName("test updated" + random.nextInt());
        playlist.setUserId(1);
        return playlist;
    }

    private Playlist getTrackByIdFirst() {
        Playlist track = new Playlist();
        track.setTrackId(1);
        return track;
    }
    private Playlist getTrackByIdSecond() {
        Playlist track = new Playlist();
        track.setTrackId(2);
        return track;
    }



}
