package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.request.PathFindRequest;
import subway.dto.request.SectionRequest;
import subway.dto.response.PathFindResponse;
import subway.error.ErrorResponse;
import subway.integration.helper.SectionIntegrationHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.helper.LineIntegrationHelper.createLine;
import static subway.integration.helper.StationIntegrationHelper.createStation;

@DisplayName("경로 조회 기능 테스트")
public class PathIntegrationTest extends IntegrationTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        교대역 = createStation("교대역").getId();
        강남역 = createStation("강남역").getId();
        양재역 = createStation("양재역").getId();
        남부터미널역 = createStation("남부터미널역").getId();

        이호선 = createLine("2호선", "green", 교대역, 강남역, 10).getId();
        신분당선 = createLine("신분당선", "red", 강남역, 양재역, 10).getId();
        삼호선 = createLine("3호선", "orange", 교대역, 남부터미널역, 2).getId();

        SectionIntegrationHelper.createSection(삼호선, new SectionRequest(남부터미널역, 양재역, 3));

    }

    @DisplayName("[GET] [/paths] 출발역 ID 와 도착역 ID 가 주어졌을 때 경로에 있는 역 목록과 조회한 경로 구간의 거리를 응답한다.")
    @Test
    void findShortPath() {
        // given
        final PathFindRequest pathFindRequest = new PathFindRequest(교대역, 양재역);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(pathFindRequest)
                .when()
                .param("source", pathFindRequest.getSource())
                .param("target", pathFindRequest.getTarget())
                .get("/paths")
                .then().log().all().
                extract();

        // then
        final PathFindResponse pathFindResponse = response.as(PathFindResponse.class);

        assertThat(pathFindResponse.getStations())
                .hasSize(3)
                .extracting("name")
                .contains(
                        "교대역",
                        "남부터미널역",
                        "양재역"
                );

        assertThat(pathFindResponse.getDistance())
                .isEqualTo(5);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("[GET] [/paths] 출발역 ID 와 도착역 ID 가 같은 경우 BAD_REQUEST 응답을 받는다.")
    @Test
    void findShortPathFailBecauseOfSameSourceAndTarget() {
        // given

        final PathFindRequest pathFindRequest = new PathFindRequest(교대역, 교대역);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(pathFindRequest)
                .when()
                .param("source", pathFindRequest.getSource())
                .param("target", pathFindRequest.getTarget())
                .get("/paths")
                .then().log().all().
                extract();

        // then
        final ErrorResponse errorResponse = response.body().as(ErrorResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("출발역과 도착역이 같은 경우 최단 거리를 구할 수 없습니다.");
    }

    @DisplayName("[GET] [/paths] 출발역 도착역이 연결되어 있지 않은 경우 BAD_REQUEST 응답을 받는다.")
    @Test
    void findShortPathFailBecauseOfDisconnectSourceAndTarget() {
        // given
        final Long 낙성대역 = createStation("낙성대").getId();
        final PathFindRequest pathFindRequest = new PathFindRequest(교대역, 낙성대역);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(pathFindRequest)
                .when()
                .param("source", pathFindRequest.getSource())
                .param("target", pathFindRequest.getTarget())
                .get("/paths")
                .then().log().all().
                extract();

        // then
        final ErrorResponse errorResponse = response.body().as(ErrorResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("출발역과 도착역이 연결되어 있지 않습니다.");
    }

    @DisplayName("[GET] [/paths] 출발역 ID 와 도착역 ID 가 존재하지 않으면 BAD_REQUEST 웅답을 받는다. ")
    @Test
    void findShortPathFailBecauseOfNotFoundSourceOrTarget() {
        // given

        final PathFindRequest pathFindRequest = new PathFindRequest(-1L, 99L);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(pathFindRequest)
                .when()
                .param("source", pathFindRequest.getSource())
                .param("target", pathFindRequest.getTarget())
                .get("/paths")
                .then().log().all().
                extract();

        // then
        final ErrorResponse errorResponse = response.body().as(ErrorResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("역을 찾을 수 없습니다.");
    }
}
