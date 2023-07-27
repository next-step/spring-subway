package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.response.PathResponse;
import subway.dto.response.StationResponse;

@DisplayName("역 사이의 최단 거리")
@ExtendWith(MockitoExtension.class)
public class PathControllerTest extends IntegrationTest {

    private Long 출발역_아이디 = 5L;
    private Long 도착역_아이디 = 8L;
    private Station 교대역 = new Station(5L, "교대역");
    private Station 강남역 = new Station(6L, "강남역");
    private Station 남부터미널역 = new Station(7L, "남부터미널역");
    private Station 양재역 = new Station(8L, "양재역");
    private Line 이호선 = new Line(2L, "2호선", "빨강");
    private Line 삼호선 = new Line(3L, "삼호선", "노랑");
    private Line 신분당선 = new Line(4L, "신분당선", "파랑");
    private Section 교대_강남 = new Section(2L, 교대역, 강남역, 이호선, 1);
    private Section 교대_남부터미널 = new Section(3L, 교대역, 남부터미널역, 삼호선, 2);
    private Section 강남_양재 = new Section(4L, 강남역, 양재역, 신분당선, 5);
    private Section 남부터미널_양재 = new Section(5L, 남부터미널역, 양재역, 삼호선, 3);

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("성공 : 역과 역사이의 최단 거리 역 정보 리턴")
    void findMinimumDistanceStations() {
        // given
        StationResponse 최단_교대역 = new StationResponse(5L, "교대역");
        StationResponse 최단_남부터미널역 = new StationResponse(7L, "남부터미널역");
        StationResponse 최단_양재역 = new StationResponse(8L, "양재역");
        Long 최단거리 = 5L;
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
        PathResponse pathResponseReal = response.as(PathResponse.class);
        assertThat(pathResponseReal.getDistance()).isEqualTo(pathResponse.getDistance());
    }

}
