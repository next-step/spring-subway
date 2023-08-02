package subway.integration.supporter;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

public class PathIntegrationSupporter {

    public static ExtractableResponse<Response> findPath(long sourceId, long targetId) {
        return given().log().all()
                .queryParam("source", sourceId)
                .queryParam("target", targetId)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
