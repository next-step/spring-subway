package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.request.LineRequest;
import subway.dto.request.SectionRegistRequest;
import subway.dto.response.PathResponse;
import subway.dto.response.StationResponse;

@DisplayName("역 사이의 최단 거리")
public class PathIntegrationTest extends IntegrationTest {

    private Long 출발역_아이디;
    private Long 도착역_아이디;
    private Station 교대역;
    private Station 강남역;
    private Station 남부터미널역;
    private Station 양재역;
    private Line 이호선;
    private Line 삼호선;
    private Line 신분당선;
    private Section 교대_강남;
    private Section 교대_남부터미널;
    private Section 강남_양재;
    private Section 남부터미널_양재;
    private LineRequest 이호선_교대_강남;
    private LineRequest 삼호선_교대_남부터미널;
    private LineRequest 신분당선_강남_양재;
    private LineRequest 삼호선_남부터미널_양재;
    private SectionRegistRequest 구간_남부터미널_양재;

    @BeforeEach
    public void setUp() {
        super.setUp();

        출발역_아이디 = 5L;
        도착역_아이디 = 8L;
        교대역 = new Station(5L, "교대역");
        강남역 = new Station(6L, "강남역");
        남부터미널역 = new Station(7L, "남부터미널역");
        양재역 = new Station(8L, "양재역");
        이호선 = new Line(2L, "2호선", "빨강");
        삼호선 = new Line(3L, "삼호선", "노랑");
        신분당선 = new Line(4L, "신분당선", "파랑");
        교대_강남 = new Section(2L, 교대역, 강남역, 이호선, 1);
        교대_남부터미널 = new Section(3L, 교대역, 남부터미널역, 삼호선, 2);
        강남_양재 = new Section(4L, 강남역, 양재역, 신분당선, 5);
        남부터미널_양재 = new Section(5L, 남부터미널역, 양재역, 삼호선, 3);

        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "교대역");
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all();
        params = new HashMap<>();
        params.put("name", "강남역");
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all();
        params = new HashMap<>();
        params.put("name", "남부터미널역");
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all();
        params = new HashMap<>();
        params.put("name", "양재역");
        RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all();

        이호선_교대_강남 = new LineRequest("2호선", "bg-red-600", 5L, 6L, 1);
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(이호선_교대_강남)
            .when().post("/lines")
            .then().log().all();
        삼호선_교대_남부터미널 = new LineRequest("3호선", "bg-red-600", 5L, 7L, 2);
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(삼호선_교대_남부터미널)
            .when().post("/lines")
            .then().log().all();
        신분당선_강남_양재 = new LineRequest("신분당선", "bg-red-600", 6L, 8L, 5);
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(신분당선_강남_양재)
            .when().post("/lines")
            .then().log().all();
        구간_남부터미널_양재 = new SectionRegistRequest(8L, 7L, 3);
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(구간_남부터미널_양재)
            .when().post("/lines/4/sections")
            .then().log().all();
    }

    @Test
    @DisplayName("성공 : 역과 역사이의 최단 거리 역 정보 리턴")
    void findMinimumDistanceStations() {
        // given
        StationResponse 최단_교대역 = new StationResponse(5L, "교대역");
        StationResponse 최단_남부터미널역 = new StationResponse(7L, "남부터미널역");
        StationResponse 최단_양재역 = new StationResponse(8L, "양재역");
        Double 최단거리 = 5D;
        PathResponse pathResponse = new PathResponse(
            List.of(최단_교대역, 최단_남부터미널역, 최단_양재역),
            최단거리
        );

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

}
