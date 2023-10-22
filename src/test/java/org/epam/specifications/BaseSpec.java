package org.epam.specifications;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

import java.net.HttpURLConnection;

public class BaseSpec {
    protected static final String localHost = "8888";
    protected static final String BASE_URL = "http://localhost:" + localHost + "/api/";

    public static final String parameterPlaylists = "playlists/";
    public static final String parameterAddTrack = "/tracks/add";
    public static final String parameterRemoveTrack = "/tracks/remove";
    public static final String parameterGetAllTracks = "/tracks";


    protected RequestSpecBuilder baseRequestBuilder = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType(ContentType.JSON)
            .addQueryParams(Authentification.getAuthentificationParameters());

    public ResponseSpecification getResponseSpecCheckCreated() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpURLConnection.HTTP_CREATED)
                .expectContentType(ContentType.JSON)
                .build();
    }
    public ResponseSpecification getResponseSpecCheckOk() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpURLConnection.HTTP_OK)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
