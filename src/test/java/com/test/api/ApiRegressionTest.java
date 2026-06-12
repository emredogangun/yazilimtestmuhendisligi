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

    // Test edeceğimiz API'nin ana adresi (Sahte veri sunan test servisi)
    private static final String BASE_URI = "https://jsonplaceholder.typicode.com";
    
    // API yanıt süresi için performans limiti (milisaniye cinsinden 5 saniye)
    private static final long MAX_RESPONSE_TIME_MS = 5000L;

    @BeforeAll
    public static void setup() {
        // Tüm testlerden önce ana API adresini tanımlıyoruz (Kod tekrarını önlemek için)
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @DisplayName("GET /posts/1 - Retrieve Single Post and Validate Fields, Status, and Performance")
    public void testGetBlogPost() {
        RestAssured.given()
                .log().all() // Gönderilen isteğin detaylarını konsola yazdır
                .accept(ContentType.JSON) // JSON formatında yanıt beklediğimizi belirt
            .when()
                .get("/posts/1") // 1 ID'li yazıyı çekmek için GET isteği at
            .then()
                .log().all() // Gelen yanıtı (Response Body ve Header) konsola yazdır
                .statusCode(200) // Sunucunun başarıyla veri döndüğünü doğrula (200 OK)
                .time(Matchers.lessThan(MAX_RESPONSE_TIME_MS)) // Performans doğrulaması (5 saniyeden kısa olmalı)
                .contentType(ContentType.JSON) // Dönen verinin JSON olduğunu doğrula
                .body("userId", Matchers.equalTo(1)) // userId değerinin 1 olduğunu doğrula
                .body("id", Matchers.equalTo(1)) // id değerinin 1 olduğunu doğrula
                .body("title", Matchers.notNullValue()) // Başlığın boş olmadığını doğrula
                .body("body", Matchers.notNullValue()); // İçeriğin boş olmadığını doğrula
    }

    @Test
    @DisplayName("POST /posts - Create New Post and Verify Response Body, Headers, and Performance")
    public void testCreateBlogPost() {
        // Sunucuya göndereceğimiz yeni blog yazısı verileri (Request Body)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Yazılım Test Mühendisliği");
        requestBody.put("body", "Rest Assured kütüphanesi ile otomatik regresyon testleri başarıyla yazıldı.");
        requestBody.put("userId", 10);

        RestAssured.given()
                .log().all()
                .contentType(ContentType.JSON) // Gönderdiğimiz verinin JSON olduğunu belirt
                .body(requestBody) // Hazırladığımız verileri isteğe ekle
            .when()
                .post("/posts") // Yeni veri oluşturmak için POST isteği gönder
            .then()
                .log().all()
                .statusCode(201) // Kayıt başarılı olunca sunucunun 201 Created dönmesini doğrula
                .time(Matchers.lessThan(MAX_RESPONSE_TIME_MS))
                .contentType(ContentType.JSON)
                .body("id", Matchers.notNullValue()) // Sunucunun yeni kayda otomatik ID atadığını doğrula
                .body("title", Matchers.equalTo("Yazılım Test Mühendisliği")) // Kaydedilen başlığı doğrula
                .body("body", Matchers.equalTo("Rest Assured kütüphanesi ile otomatik regresyon testleri başarıyla yazıldı.")) // Kaydedilen içeriği doğrula
                .body("userId", Matchers.equalTo(10)); // Kullanıcı ID'sini doğrula
    }

    @Test
    @DisplayName("PUT /posts/1 - Update Post and Validate Regression")
    public void testUpdateBlogPost() {
        // Güncelleyeceğimiz yeni verileri hazırlıyoruz
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
                .put("/posts/1") // 1 ID'li veriyi güncellemek için PUT isteği gönder
            .then()
                .log().all()
                .statusCode(200) // Güncelleme başarılıysa 200 OK döner
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
                .delete("/posts/1") // 1 ID'li veriyi silmek için DELETE isteği gönder
            .then()
                .log().all()
                .statusCode(200) // Silme başarılıysa 200 OK döner
                .time(Matchers.lessThan(MAX_RESPONSE_TIME_MS));
    }
}
