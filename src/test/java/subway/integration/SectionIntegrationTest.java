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
import subway.fixture.LineRequestFixture;
import subway.fixture.SectionRequestFixture;
import subway.fixture.StationFixture;
import subway.fixture.StationRequestFixture;
import subway.integration.supporter.LineIntegrationSupporter;
import subway.integration.supporter.SectionIntegrationSupporter;
import subway.integration.supporter.StationIntegrationSupporter;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        final long 신분당선_ID = 1L;
        create_신분당선_with_첫번째역_두번째역();

        final StationRequest 세번째역_요청 = StationRequestFixture.세번째역_요청();
        StationIntegrationSupporter.createStation(세번째역_요청);

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.createSectionInLine(
                신분당선_ID,
                SectionRequestFixture.create(2, 3)
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        ExtractableResponse<Response> 신분당선_조회_응답 = LineIntegrationSupporter.findLine(신분당선_ID);

        assertThat(신분당선_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 노선으로 인한 구간 생성 실패")
    @Test
    void createSectionWithUnmatchedLineId() {
        // given
        create_신분당선_with_첫번째역_두번째역();

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.createSectionInLine(
                2L,
                SectionRequestFixture.create(2, 3));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있어서 구간 생성 실패")
    @Test
    void createSectionWithBothExistingStation() {
        // given
        final long 신분당선_ID = 1L;
        create_신분당선_with_첫번째역_두번째역();
        final SectionRequest badRequest = SectionRequestFixture.create(1, 2);

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.createSectionInLine(신분당선_ID, badRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 모두 노선에 등록되어 있지 않아서 구간 생성 실패")
    @Test
    void createSectionWithBothNotExistingStation() {
        // given
        final long 신분당선_ID = 1L;
        create_신분당선_with_첫번째역_두번째역();
        final SectionRequest badRequest = SectionRequestFixture.create(5, 6);

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.createSectionInLine(신분당선_ID, badRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선의 하행 마지막 구간을 삭제한다.")
    @Test
    void deleteLastSection() {
        // given
        final long 신분당선_ID = 1L;
        create_신분당선_with_첫번째역_두번째역();

        final long 마지막_역_ID = 3L;
        StationIntegrationSupporter.createStation(StationRequestFixture.세번째역_요청());
        SectionIntegrationSupporter.createSectionInLine(신분당선_ID, SectionRequestFixture.create(2, 마지막_역_ID));

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.deleteSectionInLineByStationId(
                신분당선_ID, 마지막_역_ID
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선의 하행 마지막이 아닌 구간을 삭제한다.")
    @Test
    void deleteAnySectionNotLast() {
        // given
        final long 신분당선_ID = 1L;
        create_신분당선_with_첫번째역_두번째역();

        StationIntegrationSupporter.createStation(StationRequestFixture.세번째역_요청());
        SectionIntegrationSupporter.createSectionInLine(신분당선_ID, SectionRequestFixture.create(2, 3));

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.deleteSectionInLineByStationId(
                신분당선_ID, 2L
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선에 구간이 1개일 때 구간 제거 실패")
    @Test
    void deleteSectionAtLineHasOneSection() {
        // given
        final long 신분당선_ID = 1L;
        create_신분당선_with_첫번째역_두번째역();

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.deleteSectionInLineByStationId(
                신분당선_ID, 2L
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선에 해당 역이 존재하지 않을 때 구간 제거 실패")
    @Test
    void deleteSectionAtLineNotHasStation() {
        // given
        create_신분당선_with_첫번째역_두번째역();

        // when
        ExtractableResponse<Response> response = SectionIntegrationSupporter.deleteSectionInLineByStationId(
                2L, StationFixture.두번째역().getId()
        );

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void create_신분당선_with_첫번째역_두번째역() {
        final LineRequest 신분당선_요청 = LineRequestFixture.신분당선_요청();
        final StationRequest 첫번째역_요청 = StationRequestFixture.첫번째역_요청();
        final StationRequest 두번째역_요청 = StationRequestFixture.두번째역_요청();

        StationIntegrationSupporter.createStation(첫번째역_요청);
        StationIntegrationSupporter.createStation(두번째역_요청);
        LineIntegrationSupporter.createLine(신분당선_요청);
    }
}
