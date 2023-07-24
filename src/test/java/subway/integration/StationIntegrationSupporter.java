package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.StationRequest;

import static io.restassured.RestAssured.given;

public class StationIntegrationSupporter {

    static ExtractableResponse<Response> createStation(StationRequest stationRequest) {
        return given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
