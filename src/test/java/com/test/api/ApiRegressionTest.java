package com.test.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@DisplayName("API Regression Test Suite")
public class ApiRegressionTest {

    private static final String BASE_URI = "https://jsonplaceholder.typicode.com";
    private static final long MAX_RESPONSE_TIME_MS = 5000L;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @DisplayName("GET /posts/1 - Retrieve Single Post and Validate Fields, Status, and Performance")
    public void testGetBlogPost() {
        RestAssured.given()
                .log().all()
                .accept(ContentType.JSON)
            .when()
                .get("/posts/1")
            .then()
                .log().all()
                .statusCode(200)
                .time(Matchers.lessThan(MAX_RESPONSE_TIME_MS))
                .contentType(ContentType.JSON)
                .body("userId", Matchers.equalTo(1))
                .body("id", Matchers.equalTo(1))
                .body("title", Matchers.notNullValue())
                .body("body", Matchers.notNullValue());
    }

    @Test
    @DisplayName("POST /posts - Create New Post and Verify Response Body, Headers, and Performance")
    public void testCreateBlogPost() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Yazılım Test Mühendisliği");
        requestBody.put("body", "Rest Assured kütüphanesi ile otomatik regresyon testleri başarıyla yazıldı.");
        requestBody.put("userId", 10);

        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/posts")
            .then()
                .log().all()
                .statusCode(201)
                .time(Matchers.lessThan(MAX_RESPONSE_TIME_MS))
                .contentType(ContentType.JSON)
                .body("id", Matchers.notNullValue())
                .body("title", Matchers.equalTo("Yazılım Test Mühendisliği"))
                .body("body", Matchers.equalTo("Rest Assured kütüphanesi ile otomatik regresyon testleri başarıyla yazıldı."))
                .body("userId", Matchers.equalTo(10));
    }

    @Test
    @DisplayName("PUT /posts/1 - Update Post and Validate Regression")
    public void testUpdateBlogPost() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", 1);
        requestBody.put("title", "Güncellenmiş Başlık");
        requestBody.put("body", "Test kapsamında gövde güncellendi.");
        requestBody.put("userId", 1);

        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .put("/posts/1")
            .then()
                .log().all()
                .statusCode(200)
                .time(Matchers.lessThan(MAX_RESPONSE_TIME_MS))
                .body("title", Matchers.equalTo("Güncellenmiş Başlık"))
                .body("body", Matchers.equalTo("Test kapsamında gövde güncellendi."))
                .body("userId", Matchers.equalTo(1));
    }

    @Test
    @DisplayName("DELETE /posts/1 - Delete Post and Verify Status Code")
    public void testDeleteBlogPost() {
        RestAssured.given()
                .log().all()
            .when()
                .delete("/posts/1")
            .then()
                .log().all()
                .statusCode(200)
                .time(Matchers.lessThan(MAX_RESPONSE_TIME_MS));
    }
}
