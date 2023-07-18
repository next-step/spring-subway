package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.domain.Line;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;
import subway.dto.StationResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        createStation("강남역");

        createStation("역삼역");

        Long lineId = createLine("2호선", "bg-123");

        ExtractableResponse<Response> stations = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
        List<Long> stationIds = stations.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        SectionRequest params = new SectionRequest(stationIds.get(0), stationIds.get(1), 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 구간을 생성할 때 중복된 역이 있으면 예외를 던진다.")
    @Test
    void createAlreadyExist() {
        // given
        createStation("강남역");
        createStation("역삼역");
        Long lineId = createLine("2호선", "bg-123");

        ExtractableResponse<Response> stations = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();
        List<Long> stationIds = stations.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        SectionRequest params = new SectionRequest(stationIds.get(0), stationIds.get(1), 10);
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        SectionRequest params2 = new SectionRequest(stationIds.get(1), stationIds.get(0), 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static Long createLine(String name, String color) {
        Line line = new Line(name, color);
        ExtractableResponse<Response> lineResponse = RestAssured.given()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then()
                .extract();
        return lineResponse.as(LineResponse.class).getId();
    }

    private static void createStation(String name) {
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
