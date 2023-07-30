package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class PathIntegrationSupporter {

    static ExtractableResponse<Response> findPath(Long source, Long target) {
        return given().log().all()
                .when()
                .post("/paths?source={source}&target={target}", source, target)
                .then().log().all()
                .extract();
    }
}
