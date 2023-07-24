package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.integration.supporter.LineIntegrationSupporter;
import subway.integration.supporter.SectionIntegrationSupporter;
import subway.integration.supporter.StationIntegrationSupporter;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    private StationRequest stationRequest;
    private SectionRequest sectionRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();

        stationRequest = new StationRequest("서울");
        sectionRequest = new SectionRequest( "2", "3",10);
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        createInitialLine();

        StationIntegrationSupporter.createStation(stationRequest);

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.createSectionInLine(1L, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        ExtractableResponse<Response> responseForCheck = LineIntegrationSupporter.findLine(1L);

        assertThat(responseForCheck.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 노선으로 인한 구간 생성 실패")
    @Test
    void createSectionWithUnmatchedLineId() {
        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.createSectionInLine(1L, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있어서 구간 생성 실패")
    @Test
    void createSectionWithBothExistingStation() {
        // given
        createInitialLine();
        final SectionRequest badSectionRequest = new SectionRequest("1", "2", 5);

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.createSectionInLine(1L, badSectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 모두 노선에 등록되어 있지 않아서 구간 생성 실패")
    @Test
    void createSectionWithBothNotExistingStation() {
        // given
        createInitialLine();
        final SectionRequest badSectionRequest = new SectionRequest("5", "6", 5);

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.createSectionInLine(1L, badSectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        createInitialLine();

        StationIntegrationSupporter.createStation(stationRequest);
        SectionIntegrationSupporter.createSectionInLine(1L, sectionRequest);

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.deleteSectionInLineByStationId(1L, 3L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선의 하행 종점역이 아닐 때 구간 제거 실패")
    @Test
    void deleteSectionWithNotLastStation() {
        // given
        createInitialLine();

        StationIntegrationSupporter.createStation(stationRequest);
        SectionIntegrationSupporter.createSectionInLine(1L, sectionRequest);

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.deleteSectionInLineByStationId(1L, 1L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선에 구간이 1개일 때 구간 제거 실패")
    @Test
    void deleteSectionAtLineHasOneSection() {
        // given
        createInitialLine();

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.deleteSectionInLineByStationId(1L, 2L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void createInitialLine() {
        final LineRequest lineRequest = new LineRequest("1호선", 1L, 2L, 10, "blue");
        final StationRequest stationRequest1 = new StationRequest("인천");
        final StationRequest stationRequest2 = new StationRequest("부평");

        StationIntegrationSupporter.createStation(stationRequest1);
        StationIntegrationSupporter.createStation(stationRequest2);
        LineIntegrationSupporter.createLine(lineRequest);
    }
}
