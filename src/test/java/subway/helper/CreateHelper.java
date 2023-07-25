package subway.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineStationsResponse;
import subway.dto.SectionRequest;
import subway.dto.StationResponse;

public class CreateHelper {

    public static int getStationCountInLine(final Long lineId) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
        return response.jsonPath().getObject(".", LineStationsResponse.class)
                .getStations().size();
    }

    public static void createSection(final Long upStationId, final Long downStationId, Long lineId) {
        SectionRequest params = new SectionRequest(upStationId, downStationId, 10);
        RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then()
                .extract();
    }

    public static Long createLine(String name, String color, long upStationId, long downStationId) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, 10);
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
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        return response.as(StationResponse.class).getId();
    }
}
