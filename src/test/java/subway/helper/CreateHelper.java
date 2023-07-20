package subway.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.domain.Line;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;
import subway.dto.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreateHelper {

    public static List<Long> getStationIds() {
        ExtractableResponse<Response> stations = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
        return stations.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
    }

    public static void createSection(List<Long> stationIds, int index, int index1, Long lineId) {
        SectionRequest params = new SectionRequest(stationIds.get(index), stationIds.get(index1), 10);
        RestAssured.given()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then()
                .extract();
    }

    public static Long createLine(String name, String color) {
        createStation("강남역");
        createStation("역삼역");
        List<Long> stationIds = getStationIds();
        LineRequest lineRequest = new LineRequest(name, color, stationIds.get(0), stationIds.get(1), 10);
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
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
