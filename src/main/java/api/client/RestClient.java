package api.client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.http.ContentType.JSON;

public class RestClient {

    // База из твоей доки (education-services)
    protected static final String BASE_URL = "https://stellarburgers.education-services.ru";

    protected RequestSpecification baseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(JSON)
                .build();
    }

    protected RequestSpecification authSpec(String accessToken) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(JSON)
                .addHeader("Authorization", accessToken) // ожидается "Bearer ..."
                .build();
    }
}
