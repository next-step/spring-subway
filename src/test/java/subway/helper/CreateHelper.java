package subway.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.*;

public class CreateHelper {

    public static void createSection(Long upStationId, Long downStationId, int distance, Long lineId) {
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, distance);
        RestAssured.given()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then()
                .extract();
    }

    public static Long createLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);
        ExtractableResponse<Response> lineResponse = RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();
        return lineResponse.as(LineResponse.class).getId();
    }

    public static Long createStation(String name) {
        StationRequest stationRequest = new StationRequest(name);
        ExtractableResponse<Response> stationResponse = RestAssured.given()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .extract();
        return stationResponse.as(StationResponse.class).getId();
    }
}
