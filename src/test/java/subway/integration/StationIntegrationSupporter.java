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


    static ExtractableResponse<Response> findAllStations() {
        return given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> findStation(Long stationId) {
        return given().log().all()
                .when()
                .get("/stations/{stationId}", stationId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> updateStation(String uri, StationRequest stationRequest) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest)
                .when()
                .put(uri)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> deleteStation(String uri) {
        return given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
}
