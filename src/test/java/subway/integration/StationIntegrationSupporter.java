package subway.integration;

import static io.restassured.RestAssured.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.StationCreateRequest;
import subway.dto.StationUpdateRequest;

class StationIntegrationSupporter {

    static ExtractableResponse<Response> createStation(StationCreateRequest stationCreateRequest) {
        return given().log().all()
                .body(stationCreateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> findAllStation() {
        return given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> getStationByStationId(Long stationId) {
        return given().log().all()
                .when()
                .get("/stations/{stationId}", stationId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> updateStation(String uri, StationUpdateRequest stationUpdateRequest) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationUpdateRequest)
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