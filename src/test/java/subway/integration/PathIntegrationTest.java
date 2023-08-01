package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.request.LineRequest;
import subway.dto.request.SectionRegistRequest;
import subway.dto.response.StationResponse;

@DisplayName("역 사이의 최단 거리")
public class PathIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("성공 : 역과 역사이의 최단 거리 역 정보 리턴")
    void findMinimumDistanceStations() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "교대역");
        createStation(params);
        params.clear();
        params.put("name", "강남역");
        createStation(params);
        params.clear();
        params.put("name", "남부터미널역");
        createStation(params);
        params.clear();
        params.put("name", "양재역");
        createStation(params);

        LineRequest 이호선_교대_강남 = new LineRequest("2호선", "bg-red-600", 5L, 6L, 1);
        createLineAndSection(이호선_교대_강남);
        LineRequest 삼호선_교대_남부터미널 = new LineRequest("3호선", "bg-red-600", 5L, 7L, 2);
        createLineAndSection(삼호선_교대_남부터미널);
        LineRequest 신분당선_강남_양재 = new LineRequest("신분당선", "bg-red-600", 6L, 8L, 5);
        createLineAndSection(신분당선_강남_양재);
        SectionRegistRequest 구간_남부터미널_양재 = new SectionRegistRequest(8L, 7L, 3);
        Long lineId = 4L;
        createSectionByLineId(구간_남부터미널_양재, lineId);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given()
            .log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/paths?source=5&target=8")
            .then()
            .log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> stationIds = response.jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toUnmodifiableList());

        Float actualDistance = response.jsonPath().get("distance");
        assertThat(actualDistance).isEqualTo(5F);
        assertThat(stationIds).containsExactly(5L, 7L, 8L);
    }

    private void createStation(Map<String, String> params) {
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all();
    }

    private void createLineAndSection(LineRequest lineRequest) {
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when().post("/lines")
            .then().log().all();
    }

    private void createSectionByLineId(SectionRegistRequest sectionRegistRequest, Long lineId) {
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegistRequest)
            .when().post(MessageFormat.format("/lines/{0}/sections", lineId))
            .then().log().all();
    }

}
