package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.request.CreateLineRequest;
import subway.dto.request.SectionRequest;
import subway.integration.fixture.LineIntegrationFixture;
import subway.integration.fixture.SectionIntegrationFixture;
import subway.integration.fixture.StationIntegrationFixture;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    private Long lineId;
    private SectionRequest sectionRequestA;
    private SectionRequest sectionRequestB;

    @BeforeEach
    public void setUp() {
        super.setUp();

        Station upStation = StationIntegrationFixture.createStation(Map.of("name", "낙성대"));
        Station downStation = StationIntegrationFixture.createStation(Map.of("name", "사당"));
        Station newStationA = StationIntegrationFixture.createStation(Map.of("name", "방배"));
        Station newStationB = StationIntegrationFixture.createStation(Map.of("name", "서초"));

        final Line line = LineIntegrationFixture.createLine(new CreateLineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10L));

        lineId = line.getId();
        sectionRequestA = new SectionRequest(downStation.getId(), newStationA.getId(), 10L);
        sectionRequestB = new SectionRequest(newStationA.getId(), newStationB.getId(), 10L);
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequestA)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        SectionIntegrationFixture.createSection(lineId, sectionRequestA);
        SectionIntegrationFixture.createSection(lineId, sectionRequestB);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("stationId", sectionRequestB.getDownStationId())
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
