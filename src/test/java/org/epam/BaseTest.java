package org.epam;

import io.restassured.specification.RequestSpecification;
import org.epam.dto.Playlist;
import org.epam.specifications.PlaylistsSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class BaseTest {
    PlaylistsSpec playlistsSpec = new PlaylistsSpec();

    public void setCommonParams(RequestSpecification requestSpecification) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        requestSpecification.headers(headers);
    }
    public Playlist createPlaylistAPI() {
        Playlist expectedPlaylist = createPlaylist();
        var createResponse = given()
                .spec(playlistsSpec.getPlaylistCreateSpec(expectedPlaylist))
                .when()
                .post()
                .then()
                .spec(playlistsSpec.getResponseSpecCheckCreated())
                .extract().body().as(Playlist.class);
        return createResponse;
    }
    public Playlist addTrackToPlaylistAPI(int trackId, int playlistid) {
        Playlist trackToAdd = getTrackById(trackId);
        var addTrackResponse = given()
                .spec(playlistsSpec.getAddTrackToPlaylistSpec(playlistid, trackToAdd))
                .when()
                .post()
                .then()
                .spec(playlistsSpec.getResponseSpecCheckOk());
        return trackToAdd;
    }
    public Playlist createPlaylist() {
        Random random = new Random();
        Playlist playlist = new Playlist();
        playlist.setDescription("descripion" + random.nextInt());
        playlist.setPublic(true);
        playlist.setName("test" + random.nextInt());
        playlist.setUserId(1);
        return playlist;
    }
    public Playlist updatePlaylist() {
        Random random = new Random();
        Playlist playlist = new Playlist();
        playlist.setDescription("descripion updated" + random.nextInt());
        playlist.setPublic(true);
        playlist.setName("test updated" + random.nextInt());
        playlist.setUserId(1);
        return playlist;
    }
    public Playlist getTrackById(int id) {
        Playlist track = new Playlist();
        track.setTrackId(id);
        return track;
    }
}
