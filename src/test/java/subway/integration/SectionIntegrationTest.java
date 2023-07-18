package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
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
import subway.domain.Section;
import subway.domain.Station;

@DisplayName("Section 통합 테스트")
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
        lineDao.insert(lineA);
        stationDao.insert(stationA);
        stationDao.insert(stationB);
        stationDao.insert(stationC);
        Section section = new Section(lineA, stationA, stationB, 2);
        sectionDao.save(section);
        Map<String, String> params = createSectionAdditionRequest();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", lineA.getId())
            .when()
            .post("/lines/{id}/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void setupTargetLineAndStations() {
        lineDao.insert(lineA);
        stationDao.insert(stationA);
        stationDao.insert(stationB);
        stationDao.insert(stationC);
        Section section = new Section(lineA, stationA, stationB, 2);
        sectionDao.save(section);
    }

    private Map<String, String> createSectionAdditionRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", String.valueOf(stationB.getId()));
        params.put("downStationId", String.valueOf(stationC.getId()));
        params.put("distance", "3");
        return params;
    }


}
