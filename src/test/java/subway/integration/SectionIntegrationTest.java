package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.SectionAdditionRequest;

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
        LineRequest lineRequest = new LineRequest("신분당선", 1L, 2L, 3, "bg-red-600");
        LineIntegrationTest.createLine(lineRequest);
        SectionAdditionRequest sectionAdditionRequest = new SectionAdditionRequest(stationB.getId(),
            stationC.getId(), 3);

        // when
        ExtractableResponse<Response> response = addSectionToLine(sectionAdditionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private ExtractableResponse<Response> addSectionToLine(
        SectionAdditionRequest sectionAdditionRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(sectionAdditionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", lineA.getId())
            .when()
            .post("/lines/{id}/sections")
            .then().log().all()
            .extract();
        return response;
    }

    @DisplayName("지하철 노선의 구간을 제거한다.")
    @Test
    void removeSectionOfLine() {
        // given
        StationIntegrationTest.createInitialStations();
        LineRequest lineRequest = new LineRequest("신분당선", 1L, 2L, 3, "bg-red-600");
        LineIntegrationTest.createLine(lineRequest);
        SectionAdditionRequest sectionAdditionRequest = new SectionAdditionRequest(stationB.getId(),
            stationC.getId(), 3);
        addSectionToLine(sectionAdditionRequest);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", lineA.getId())
            .param("stationId", stationB.getId())
            .when().delete("/lines/{id}/sections")
            .then().log().all().
            extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
