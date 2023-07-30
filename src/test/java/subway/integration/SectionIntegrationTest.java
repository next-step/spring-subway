package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.DomainFixtures.createInitialLine;
import static subway.DomainFixtures.createStation;
import static subway.DomainFixtures.extendSectionToLine;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.DomainFixtures.LineWithStationId;
import subway.RestApiUtils;
import subway.ui.dto.SectionRequest;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "부평", "인천");
        long seoulId = createStation("서울");

        // when
        final SectionRequest extendToSeoul = new SectionRequest(
            String.valueOf(lineNo1.getDownStationId()),
            String.valueOf(seoulId), 10);
        ExtractableResponse<Response> response = RestApiUtils.post(extendToSeoul,
            "/lines/" + lineNo1.getLineId() + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

        ExtractableResponse<Response> responseForCheck = RestApiUtils.get(
            "/lines/" + lineNo1.getLineId());
        assertThat(responseForCheck.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선에 존재하지 않는 역으로 인한 구간 생성 실패")
    @Test
    void createSectionWithUnmatchedLineId() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "부평", "인천");
        long notExistStationId = createStation("서울");
        long newStationId = createStation("남영");
        SectionRequest extend = new SectionRequest(String.valueOf(notExistStationId),
            String.valueOf(newStationId), 10);

        // when
        ExtractableResponse<Response> response = RestApiUtils.post(extend,
            "/lines/" + lineNo1.getLineId() + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있어서 구간 생성 실패")
    @Test
    void createSectionWithBothExistingStation() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "부평", "인천");

        final String duplicateUpStationId = String.valueOf(lineNo1.getUpStationId());
        final String duplicateDownStationId = String.valueOf(lineNo1.getDownStationId());
        final SectionRequest badSectionRequest = new SectionRequest(duplicateUpStationId,
            duplicateDownStationId, 5);

        // when
        ExtractableResponse<Response> response = RestApiUtils.post(badSectionRequest,
            "/lines/" + lineNo1.getLineId() + "/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 모두 노선에 등록되어 있지 않아서 구간 생성 실패")
    @Test
    void createSectionWithBothNotExistingStation() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "부평", "인천");

        final String notExistUpStationId = String.valueOf(lineNo1.getDownStationId() + 1);
        final String notExistDownStationId = String.valueOf(lineNo1.getDownStationId() + 2);
        final SectionRequest badSectionRequest = new SectionRequest(notExistUpStationId,
            notExistDownStationId, 5);

        // when
        ExtractableResponse<Response> response = RestApiUtils.post(badSectionRequest,
            "/lines/"+ lineNo1.getLineId() +"/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 존재하지 않는 역을 삭제한다.")
    @Test
    void deleteNotExistSectionInLine() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "부평", "인천");
        long seoul = createStation("남영");
        long lastSectionId = extendSectionToLine(lineNo1.getLineId(), lineNo1.getDownStationId(),
            seoul);
        final long notExistSectionId = lastSectionId + 1;

        // when
        ExtractableResponse<Response> response = RestApiUtils.delete(
            "/lines/"+ lineNo1.getLineId() +"/sections?stationId=" + notExistSectionId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 중간 구간을 삭제한다.")
    @Test
    void deleteMiddleSection() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "부평", "인천");
        long seoul = createStation("남영");
        extendSectionToLine(lineNo1.getLineId(), lineNo1.getDownStationId(), seoul);

        // when
        long middleStationId = lineNo1.getDownStationId();
        ExtractableResponse<Response> response = RestApiUtils.delete(
            "/lines/" + lineNo1.getLineId() + "/sections?stationId=" + middleStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> responseForCheck = RestApiUtils.get("/lines/"+ lineNo1.getLineId());
        assertThat(responseForCheck.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 종점 구간을 삭제한다.")
    @Test
    void deleteLastSection() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "부평", "인천");
        long lastStationId = createStation("남영");
        extendSectionToLine(lineNo1.getLineId(), lineNo1.getDownStationId(), lastStationId);

        // when
        ExtractableResponse<Response> response = RestApiUtils.delete(
            "/lines/" + lineNo1.getLineId() + "/sections?stationId=" + lastStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> responseForCheck = RestApiUtils.get(
            "/lines/" + lineNo1.getLineId());
        assertThat(responseForCheck.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선에 구간이 1개일 때 구간 제거 실패")
    @Test
    void deleteSectionAtLineHasOneSection() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "부평", "인천");

        // when
        ExtractableResponse<Response> response = RestApiUtils.delete(
            "/lines/"+ lineNo1.getLineId() + "/sections?stationId=" + lineNo1.getDownStationId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
