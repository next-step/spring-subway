package subway.integration;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

class PathIntegrationSupporter {

    private PathIntegrationSupporter() {
        throw new UnsupportedOperationException("Cannot invoke constructor \"PathIntegrationSupporter()\"");
    }

    static ExtractableResponse<Response> findStationPath(long sourceStationId, long targetStationId) {
        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(ContentType.JSON)
                .when().log().all()
                .get("/paths?source={sourceStationId}&target={targetStationId}", sourceStationId, targetStationId)
                .then().log().all()
                .extract();
    }

}
