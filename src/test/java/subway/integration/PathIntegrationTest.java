package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(value = "classpath:/path-testdata.sql")
@DisplayName("경로 조회 기능")
public class PathIntegrationTest extends IntegrationTest {

    @Autowired
    private StationDao stationDao;

    @DisplayName("주어진 역 사이의 최단 경로를 조회한다")
    @MethodSource("findShortestPathSource")
    @ParameterizedTest(name = "{3}")
    void findShortestPath(Long departureStationId, Long arrivalStationId, PathResponse expectedPathResponse, String displayName) {
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
                .isEqualTo(expectedPathResponse);
    }

    private Stream<Arguments> findShortestPathSource() {
        return Stream.of(
                Arguments.of(1, 3,
                        new PathResponse(getStationResponses(1, 2, 3), 5, 1_250),
                        "id=1에서 id=3까지의 최단 경로"),
                Arguments.of(1, 5,
                        new PathResponse(getStationResponses(1, 2, 3, 4, 5), 18, 1_450),
                        "id=1에서 id=5까지의 최단 경로"),
                Arguments.of(1, 7,
                        new PathResponse(getStationResponses(1, 2, 3, 4, 5, 6, 7), 78, 2_450),
                        "id=1에서 id=7까지의 최단 경로"),
                Arguments.of(1, 10,
                        new PathResponse(getStationResponses(1, 2, 3, 9, 10), 15, 1_350),
                        "id=1에서 id=10까지의 최단 경로"),
                Arguments.of(1, 11,
                        new PathResponse(getStationResponses(1, 2, 3, 9, 10, 11), 19, 1_450),
                        "id=1에서 id=11까지의 최단 경로"),
                Arguments.of(10, 14,
                        new PathResponse(getStationResponses(10, 9, 3, 4, 5, 14), 29, 1_650),
                        "id=10에서 id=14까지의 최단 경로"),
                Arguments.of(14, 10,
                        new PathResponse(getStationResponses(14, 5, 4, 3, 9, 10), 29, 1_650),
                        "id=14에서 id=10까지의 최단 경로"),
                Arguments.of(4, 16,
                        new PathResponse(getStationResponses(4, 5, 14, 15, 16), 18, 1_450),
                        "id=4에서 id=16까지의 최단 경로")
        );
    }

    private List<StationResponse> getStationResponses(long... ids) {
        List<StationResponse> list = new ArrayList<>();
        for (Long id : ids) {
            Station station = stationDao.findById(id).get();
            list.add(StationResponse.of(station));
        }
        return list;
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getPath())
                .flatExtracting(StationResponse::getId).containsOnly(1);
        assertThat(pathResponse.getTotalDistance()).isEqualTo(0);
        assertThat(pathResponse.getPrice()).isEqualTo(0);
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
