package subway.integration.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class CommonIntegrationFixture {

    public static ExtractableResponse<Response> get(String url) {
        return RestAssured.given().log().all()
            .when()
            .get(url)
            .then().log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> get(String url, T pathParam) {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get(url, pathParam)
            .then().log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> post(String url, T body) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(url)
            .then().log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> post(String url, T pathParam, T body) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(url, pathParam)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> delete(String url) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(url)
            .then().log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> delete(String url, T pathParam) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .delete(url, pathParam)
            .then().log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> delete(String url, T pathParam, String parameterName, T parameterValue) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param(parameterName, parameterValue)
            .when()
            .delete(url, pathParam)
            .then().log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> put(String url, T body) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(url)
            .then().log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> put(String url, T pathParam, T body) {
        return RestAssured.given().log().all()
            .body(body)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(url, pathParam)
            .then().log().all()
            .extract();
    }
}
