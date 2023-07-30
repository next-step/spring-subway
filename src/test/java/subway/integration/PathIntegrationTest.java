package subway.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.PathResponse;
import subway.helper.CreateHelper;
import subway.helper.RestAssuredHelper;

@DisplayName("경로 조회 기능")
class PathIntegrationTest extends IntegrationTest {

    private Long 교대역_ID;
    private Long 강남역_ID;
    private Long 남부터미널역_ID;
    private Long 양재역_ID;
    private Long 일호선_ID;
    private Long 이호선_ID;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        /**
         * 교대역    --- 1km ---    강남역
         * |                        |
         * 100km                    1km
         * |                        |
         * 남부터미널역 --- 100km ---  양재역
         */
        교대역_ID = CreateHelper.createStation("교대역");
        강남역_ID = CreateHelper.createStation("강남역");
        남부터미널역_ID = CreateHelper.createStation("남부터미널역");
        양재역_ID = CreateHelper.createStation("양재역");

        일호선_ID = CreateHelper.createLine("1호선", "bg-blue", 교대역_ID, 강남역_ID, 1);
        CreateHelper.createSection(강남역_ID, 양재역_ID, 일호선_ID, 1);

        이호선_ID = CreateHelper.createLine("2호선", "bg-green", 교대역_ID, 남부터미널역_ID, 100);
        CreateHelper.createSection(남부터미널역_ID, 양재역_ID, 이호선_ID, 100);
    }

    @DisplayName("경로 조회 시 최단 경로를 제공한다.")
    @Test
    void searchPath() {
        // given
        final Map<String, Object> params = new HashMap<>();
        params.put("source", 교대역_ID);
        params.put("target", 양재역_ID);

        // when
        final ExtractableResponse<Response> response = RestAssuredHelper.get("/paths", params);
        final PathResponse body = response.as(PathResponse.class);

        // then
        assertEquals(HttpStatus.OK.value(), response.statusCode());
        assertEquals(2, body.getDistance());
        assertEquals(3, body.getStations().size());
    }

    @DisplayName("출발역과 도착역이 같은 경우 예외를 던진다.")
    @Test
    void searchPath_departureEqualsToArrival() {
        // given
        final Map<String, Object> params = new HashMap<>();
        params.put("source", 교대역_ID);
        params.put("target", 교대역_ID);

        // when
        final ExtractableResponse<Response> response = RestAssuredHelper.get("/paths", params);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    @DisplayName("경로가 존재하지 않을 경우 예외를 던진다.")
    @Test
    void searchPath_notConnected() {
        // given
        final Long 면목역_ID = CreateHelper.createStation("면목역");
        final Long 상봉역_ID = CreateHelper.createStation("상봉역");
        CreateHelper.createLine("7호선", "bg-sea-weed", 면목역_ID, 상봉역_ID, 1);

        final Map<String, Object> params = new HashMap<>();
        params.put("source", 교대역_ID);
        params.put("target", 상봉역_ID);

        // when
        final ExtractableResponse<Response> response = RestAssuredHelper.get("/paths", params);

        // then
        assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
    }

    @DisplayName("출발역이 존재하지 않을 경우 예외를 던진다.")
    @Test
    void searchPath_notExistsDeparture() {
        // given
        final Map<String, Object> params = new HashMap<>();
        params.put("source", 9999L);
        params.put("target", 교대역_ID);

        // when
        final ExtractableResponse<Response> response = RestAssuredHelper.get("/paths", params);

        // then
        assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
    }

    @DisplayName("도착역이 존재하지 않을 경우 예외를 던진다.")
    @Test
    void searchPath_notExistsArrival() {
        // given
        final Map<String, Object> params = new HashMap<>();
        params.put("source", 교대역_ID);
        params.put("target", 9999L);

        // when
        final ExtractableResponse<Response> response = RestAssuredHelper.get("/paths", params);

        // then
        assertEquals(HttpStatus.NOT_FOUND.value(), response.statusCode());
    }

    @DisplayName("출발역이 비어있을 경우 예외를 던진다.")
    @Test
    void searchPath_nullDeparture() {
        // given
        final Map<String, Object> params = new HashMap<>();
//        params.put("source", 교대역_ID);
        params.put("target", 양재역_ID);

        // when
        final ExtractableResponse<Response> response = RestAssuredHelper.get("/paths", params);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    @DisplayName("도착역이 비어있을 경우 예외를 던진다.")
    @Test
    void searchPath_nullArrival() {
        // given
        final Map<String, Object> params = new HashMap<>();
        params.put("source", 교대역_ID);
//        params.put("target", 양재역_ID);

        // when
        final ExtractableResponse<Response> response = RestAssuredHelper.get("/paths", params);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    @DisplayName("출발역에 숫자가 아닌 문자가 포함되어 있을 경우 예외를 던진다.")
    @Test
    void searchPath_departureIncludeCharacter() {
        // given
        final Map<String, Object> params = new HashMap<>();
        params.put("source", "1,");
        params.put("target", 양재역_ID);

        // when
        final ExtractableResponse<Response> response = RestAssuredHelper.get("/paths", params);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }

    @DisplayName("도착역에 숫자가 아닌 문자가 포함되어 있을 경우 예외를 던진다.")
    @Test
    void searchPath_arrivalIncludeCharacter() {
        // given
        final Map<String, Object> params = new HashMap<>();
        params.put("source", 교대역_ID);
        params.put("target", "3,");

        // when
        final ExtractableResponse<Response> response = RestAssuredHelper.get("/paths", params);

        // then
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
    }
}
