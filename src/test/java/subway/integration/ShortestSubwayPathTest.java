package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.helper.SubWayHelper.addSectionToLine;
import static subway.integration.helper.SubWayHelper.createLine;
import static subway.integration.helper.SubWayHelper.createStation;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.ShortestSubwayPath;
import subway.dto.request.LineCreationRequest;
import subway.dto.request.SectionAdditionRequest;
import subway.integration.helper.SubWayHelper;

@DisplayName("지하철 경로 탐색 통합 테스트")
class ShortestSubwayPathTest extends IntegrationTest {

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
    @BeforeEach
    public void setUp() {
        super.setUp();
        교대역 = createStation("교대역").jsonPath().getLong("id");
        강남역 = createStation("강남역").jsonPath().getLong("id");
        양재역 = createStation("양재역").jsonPath().getLong("id");
        남부터미널역 = createStation("남부터미널역").jsonPath().getLong("id");

        이호선 = createLine(new LineCreationRequest("2호선", 교대역, 강남역, 2, "green")).jsonPath().getLong("id");
        신분당선 = createLine(new LineCreationRequest("신분당선", 강남역, 양재역, 2, "red")).jsonPath().getLong("id");
        삼호선 = createLine(new LineCreationRequest("3호선", 교대역, 남부터미널역, 10, "orange")).jsonPath().getLong("id");

        addSectionToLine(삼호선, new SectionAdditionRequest(남부터미널역, 양재역, 10));
    }

    @Test
    @DisplayName("지하철 최단 거리 구하기")
    void calculateShortestPath() {

        ExtractableResponse<Response> response = SubWayHelper.calculateShortestSubwayPath(양재역, 교대역);

        ShortestSubwayPath expectedShortestSubwayPath = response.body().as(ShortestSubwayPath.class);
        assertThat(expectedShortestSubwayPath.getStations()).extracting("id")
            .containsExactly(양재역, 강남역, 교대역);
        assertThat(expectedShortestSubwayPath.getStations()).extracting("name")
            .containsExactly("양재역", "강남역", "교대역");
        assertThat(expectedShortestSubwayPath.getDistance()).isEqualTo(4);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
