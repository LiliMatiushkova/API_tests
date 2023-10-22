package org.epam.specifications;

import io.restassured.specification.RequestSpecification;
import org.epam.dto.Playlist;

public class PlaylistsSpec extends BaseSpec {
    public RequestSpecification getPlaylistCreateSpec(Playlist playlist) {
        return baseRequestBuilder
                .setBasePath(parameterPlaylists)
                .setBody(playlist)
                .build();
    }
    public RequestSpecification getPlaylistUpdateSpec(int playlistId, Playlist updatedPlaylist) {
        return baseRequestBuilder
                .setBasePath(parameterPlaylists + playlistId)
                .setBody(updatedPlaylist)
                .build();
    }
    public RequestSpecification getAddTrackToPlaylistSpec(int playlistId, Playlist trackId) {
        return baseRequestBuilder
                .setBasePath(parameterPlaylists + playlistId + parameterAddTrack)
                .setBody(trackId)
                .build();
    }
    public RequestSpecification getPlaylistGetByIdSpec(int playlistId) {
        return baseRequestBuilder
                .setBasePath(parameterPlaylists + playlistId)
                .build();
    }

    public RequestSpecification getRemoveTrackFromPlaylistSpec(int playlistId, Playlist trackId) {
        return baseRequestBuilder
                .setBasePath(parameterPlaylists + playlistId + parameterRemoveTrack)
                .setBody(trackId)
                .build();
    }

    public RequestSpecification getAllTracksFromPlaylistSpec(int playlistId) {
        return baseRequestBuilder
                .setBasePath(parameterPlaylists + playlistId + parameterGetAllTracks)
                .build();
    }

    public RequestSpecification getDeletePlaylistSpec(int playlistId) {
        return baseRequestBuilder
                .setBasePath(parameterPlaylists + playlistId)
                .build();
    }

    public RequestSpecification getAllPlaylistSpec() {
        return baseRequestBuilder
                .setBasePath(parameterPlaylists)
                .build();
    }

}
