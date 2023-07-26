package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.request.LineCreationRequest;
import subway.dto.request.SectionAdditionRequest;
import subway.integration.fixture.SubWayFixture;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @Autowired
    SectionDao sectionDao;

    @Autowired
    StationDao stationDao;

    @Autowired
    LineDao lineDao;

    Line lineA;
    Station stationA;
    Station stationB;
    Station stationC;
    Station stationD;

    @BeforeEach
    public void setUp() {
        super.setUp();
        lineA = new Line(1L, "A", "red");
        stationA = new Station(1L, "A");
        stationB = new Station(2L, "B");
        stationC = new Station(3L, "C");
        stationD = new Station(4L, "D");
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSectionToLine() {
        // given
        StationIntegrationTest.createInitialStations();
        LineCreationRequest lineCreationRequest = new LineCreationRequest("신분당선", 1L, 2L, 3,
            "bg-red-600");
        SubWayFixture.createLine(lineCreationRequest);
        SectionAdditionRequest sectionAdditionRequest = new SectionAdditionRequest(stationB.getId(),
            stationC.getId(), 3);

        // when
        ExtractableResponse<Response> response = SubWayFixture.addSectionToLine(lineA,
            sectionAdditionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("지하철 노선의 첫 역을 제거한다.")
    @Test
    void removeFirstStationOfLine() {
        // given
        StationIntegrationTest.createInitialStations();
        LineCreationRequest lineCreationRequest = new LineCreationRequest("신분당선", 1L, 2L, 3,
            "bg-red-600");
        SubWayFixture.createLine(lineCreationRequest);
        SectionAdditionRequest sectionAdditionRequest = new SectionAdditionRequest(stationB.getId(),
            stationC.getId(), 3);
        SubWayFixture.addSectionToLine(lineA, sectionAdditionRequest);

        // when
        ExtractableResponse<Response> response = SubWayFixture.removeStationOfLine(lineA, stationA);


        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선의 중간 역을 제거한다.")
    @Test
    void removeMiddleStationOfLine() {
        // given
        StationIntegrationTest.createInitialStations();
        LineCreationRequest lineCreationRequest = new LineCreationRequest("신분당선", 1L, 2L, 3,
            "bg-red-600");
        SubWayFixture.createLine(lineCreationRequest);
        SectionAdditionRequest sectionAdditionRequest = new SectionAdditionRequest(stationB.getId(),
            stationC.getId(), 3);
        SubWayFixture.addSectionToLine(lineA, sectionAdditionRequest);

        // when
        ExtractableResponse<Response> response = SubWayFixture.removeStationOfLine(lineA, stationB);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선의 마지막 역을 제거한다.")
    @Test
    void removeLastStationOfLine() {
        // given
        StationIntegrationTest.createInitialStations();
        LineCreationRequest lineCreationRequest = new LineCreationRequest("신분당선", 1L, 2L, 3,
            "bg-red-600");
        SubWayFixture.createLine(lineCreationRequest);
        SectionAdditionRequest sectionAdditionRequest = new SectionAdditionRequest(stationB.getId(),
            stationC.getId(), 3);
        SubWayFixture.addSectionToLine(lineA, sectionAdditionRequest);

        // when
        ExtractableResponse<Response> response = SubWayFixture.removeStationOfLine(lineA, stationC);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
