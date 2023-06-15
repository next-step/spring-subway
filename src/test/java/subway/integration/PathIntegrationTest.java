package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("경로 조회 기능")
public class PathIntegrationTest extends IntegrationTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        addTestData();
    }

    private void addTestData() {
        List<LineRequest> lineRequests = List.of(
                new LineRequest("3호선", "bg-orange-600"),
                new LineRequest("신분당선", "bg-red-600"),
                new LineRequest("수인분당선", "bg-yellow-600"),
                new LineRequest("7호선", "bg-green-600")
        );
        sendPostRequests("/lines", lineRequests);

        List<StationRequest> stationRequests = List.of(
                new StationRequest("교대역"),
                new StationRequest("남부터미널역"),
                new StationRequest("양재역"),
                new StationRequest("매봉역"),
                new StationRequest("도곡역"),
                new StationRequest("대치역"),
                new StationRequest("학여울역"),
                new StationRequest("강남역"),
                new StationRequest("양재시민의숲역"),
                new StationRequest("청계산입구역"),
                new StationRequest("판교역"),
                new StationRequest("선릉역"),
                new StationRequest("한티역"),
                new StationRequest("구룡역"),
                new StationRequest("개포동역"),
                new StationRequest("수내역"),
                new StationRequest("총신대입구역"),
                new StationRequest("내방역")
        );
        sendPostRequests("/stations", stationRequests);

        List<SectionRequest> line1SectionRequests = List.of(
                new SectionRequest(1L, 2L, 2),
                new SectionRequest(2L, 3L, 3),
                new SectionRequest(3L, 4L, 6),
                new SectionRequest(4L, 5L, 7),
                new SectionRequest(5L, 6L, 50),
                new SectionRequest(6L, 7L, 10)
        );
        sendPostRequests("/lines/1/sections", line1SectionRequests);

        List<SectionRequest> line2SectionRequests = List.of(
                new SectionRequest(8L, 3L, 5),
                new SectionRequest(3L, 9L, 7),
                new SectionRequest(9L, 10L, 3),
                new SectionRequest(10L, 11L, 4)
        );
        sendPostRequests("/lines/2/sections", line2SectionRequests);

        List<SectionRequest> line3SectionRequests = List.of(
                new SectionRequest(12L, 13L, 1),
                new SectionRequest(13L, 5L, 2),
                new SectionRequest(5L, 14L, 6),
                new SectionRequest(14L, 15L, 2),
                new SectionRequest(15L, 16L, 3)
        );
        sendPostRequests("/lines/3/sections", line3SectionRequests);

        List<SectionRequest> line4SectionRequests = List.of(
                new SectionRequest(17L, 18L, 4)
        );
        sendPostRequests("/lines/4/sections", line4SectionRequests);
    }

    private void sendPostRequests(String url, List<?> requestBodies) {
        for (Object requestBody : requestBodies) {
            RestAssured
                    .given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestBody)
                    .when().post(url)
                    .then();
        }
    }

    @DisplayName("주어진 역 사이의 최단 경로를 조회한다")
    @MethodSource("subway.application.path.FindShortestPathParam#findShortestPathSource")
    @ParameterizedTest(name = "{3}")
    void findShortestPath(long departureStationId, long arrivalStationId, PathResponse expectedPathResponse, String displayName) {
        // given, when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .param("departureStationId", departureStationId)
                .param("arrivalStationId", arrivalStationId)
                .when().get("/path")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse)
                .usingRecursiveComparison()
                .ignoringFields("path")
                .isEqualTo(expectedPathResponse);

        List<Long> stationIds = pathResponse.getPath().stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(pathResponse.getPath())
                .flatExtracting(StationResponse::getId).isEqualTo(stationIds);
    }

    @DisplayName("동일한 역 사이의 최단 경로를 조회한다")
    @Test
    void findShortestPathWithSameStation() {
        // given, when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .param("departureStationId", 1)
                .param("arrivalStationId", 1)
                .when().get("/path")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 역 사이의 최단 경로를 조회한다")
    @Test
    void findShortestPathWithNonExistenceStation() {
        // given, when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .param("departureStationId", 1)
                .param("arrivalStationId", 100)
                .when().get("/path")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("경로가 존재하지 않는 역 사이의 최단 경로를 조회한다")
    @Test
    void findShortestPathWithNonExistencePath() {
        // given, when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .param("departureStationId", 1)
                .param("arrivalStationId", 17)
                .when().get("/path")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
