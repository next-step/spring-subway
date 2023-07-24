package subway.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;

import java.util.HashMap;
import java.util.Map;

public class CreateHelper {

    public static void createSection(Long upStationId, Long downStationId, Long lineId) {
        SectionRequest params = new SectionRequest(upStationId, downStationId, 10);
        RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then()
                .extract();
    }

    public static Long createLine(String name, String color, String upStationName, String downStationName) {
        createStation(upStationName);
        createStation(downStationName);
        LineRequest lineRequest = new LineRequest(name, color, 1L, 2L, 10);
        ExtractableResponse<Response> lineResponse = RestAssured.given()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();
        return lineResponse.as(LineResponse.class).getId();
    }

    public static void createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .extract();
    }
}
