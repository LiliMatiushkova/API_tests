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


public class APITests extends BaseTest{
    private RequestSpecification requestSpecification;

    @BeforeMethod
    public void authSetUp() {
        requestSpecification = given().auth().oauth2("8d3edc50fd5dbb75c78aa0e6b003827314f21f4aa8f03facd79465c96ce44c55");
        setCommonParams(requestSpecification);
    }

    @Test
    public void createPlaylistTest() {
        Playlist expectedPlaylist = createPlaylist();
        var createResponse = given()
                .spec(playlistsSpec.getPlaylistCreateSpec(expectedPlaylist))
                .when()
                .post()
                .then()
                .spec(playlistsSpec.getResponseSpecCheckCreated())
                .extract().body().as(Playlist.class);
        Assert.assertEquals(createResponse.getName(), expectedPlaylist.getName());
    }

    @Test
    public void updatePlaylistTest() {
        createPlaylistAPI();
        Playlist newPlaylist = updatePlaylist();
        int createdPlaylistId = createPlaylistAPI().getId();
        var updateResponse = given()
                .spec(playlistsSpec.getPlaylistUpdateSpec(createdPlaylistId, newPlaylist))
                .when()
                .put()
                .then()
                .spec(playlistsSpec.getResponseSpecCheckOk())
                .extract().body().as(Playlist.class);
        Assert.assertEquals(updateResponse.getName(), newPlaylist.getName(),
                "Updated user doesn't have the correct name");
        Assert.assertEquals(updateResponse.getDescription(), newPlaylist.getDescription(),
                "Updated user doesn't have the correct description");
        Assert.assertEquals(updateResponse.getPublic(), newPlaylist.getPublic(),
                "Updated user doesn't have the correct public");
    }

    @Test
    public void addTracksToPlaylistTest() {
        createPlaylistAPI();
        int createdPlaylistId = createPlaylistAPI().getId();
        Playlist trackToAdd = getTrackById(1);
        var addTrackResponse = given()
                .spec(playlistsSpec.getAddTrackToPlaylistSpec(createdPlaylistId, trackToAdd))
                .when()
                .post()
                .then()
                .spec(playlistsSpec.getResponseSpecCheckOk());
        var getPlaylistResponse = given()
                .spec(playlistsSpec.getPlaylistGetByIdSpec(createdPlaylistId))
                .when()
                .get()
                .then()
                .spec(playlistsSpec.getResponseSpecCheckOk())
                .extract().body().as(Playlist.class);
        System.out.println(getPlaylistResponse.getTracks());
        Assert.assertTrue(getPlaylistResponse.getTracks().contains(trackToAdd),"Expected tracks list should contain the track with id " + trackToAdd.getTrackId() );
    }

    @Test
    public void removeTrackFromPlaylist() {
        createPlaylistAPI();
        int createdPlaylistId = createPlaylistAPI().getId();
        addTrackToPlaylistAPI(2,createdPlaylistId);
        addTrackToPlaylistAPI(3,createdPlaylistId);
        var removeTrackResponse = given()
                .spec(playlistsSpec.getRemoveTrackFromPlaylistSpec(createdPlaylistId,addTrackToPlaylistAPI(2,createdPlaylistId)))
                .when()
                .delete()
                .then()
                .spec(playlistsSpec.getResponseSpecCheckOk());
        var getAllTracksResponse = given()
                .spec(playlistsSpec.getAllTracksFromPlaylistSpec(createdPlaylistId))
                .when()
                .get()
                .then()
                .spec(playlistsSpec.getResponseSpecCheckOk());
        List<Track> tracks = getAllTracksResponse.extract().jsonPath().getList("", Track.class);
        Assert.assertFalse(tracks.contains(addTrackToPlaylistAPI(1,createdPlaylistId).getTrackId()));
   }

    @Test
    public void removePlaylist() {
        createPlaylistAPI();
        int createdPlaylistId = createPlaylistAPI().getId();
        var removePlaylistResponse = given()
                .spec(playlistsSpec.getDeletePlaylistSpec(createdPlaylistId))
                .when()
                .delete()
                .then()
                .spec(playlistsSpec.getResponseSpecCheckOk());
        var getAllPlaylistsResponse = given()
                .spec(playlistsSpec.getAllPlaylistSpec())
                .when()
                .get()
                .then()
                .spec(playlistsSpec.getResponseSpecCheckOk());
        List<Playlist> playlists = getAllPlaylistsResponse.extract().jsonPath().getList("", Playlist.class);
        Assert.assertFalse(playlists.contains(createPlaylistAPI()));
    }
}
