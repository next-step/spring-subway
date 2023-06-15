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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import subway.application.path.PathService;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(value = "classpath:/path-testdata.sql")
@DisplayName("경로 조회 기능")
public class PathIntegrationTest extends IntegrationTest {

    @Autowired
    private StationDao stationDao;
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private PathService pathService;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        addExistingDataToGraph();
    }

    private void addExistingDataToGraph() {
        List<Station> stations = stationDao.findAll();
        for (Station station : stations) {
            pathService.addVertex(station.getId());
        }

        List<Section> sections = sectionDao.findAll();
        for (Section section : sections) {
            pathService.addEdge(section.getUpStation().getId(), section.getDownStation().getId(), section.getDistance());
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
