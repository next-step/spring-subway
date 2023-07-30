package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.utils.Fixtures.createInitialLine;
import static subway.utils.Fixtures.createStation;
import static subway.utils.Fixtures.extendSectionToLine;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.utils.Fixtures.LineWithStationId;
import subway.utils.RestApi;

@DisplayName("지하철 구간 조회 관련 기능")
class PathIntegrationTest extends IntegrationTest {

    @DisplayName("최단 경로와 거리를 조회하면 OK 상태를 반환한다.")
    @Test
    void showPaths_returnStatusOK() {
        // given
        final LineWithStationId lineNo1 = createInitialLine("1호선", "인천", "부평");
        final LineWithStationId lineNo2 = createInitialLine("2호선", "잠실", "잠실나루");

        final long sindorimId = createStation("신도림");
        extendSectionToLine(lineNo1.getLineId(), lineNo1.getDownStationId(), sindorimId);
        extendSectionToLine(lineNo2.getLineId(), lineNo2.getDownStationId(), sindorimId);

        // when
        long sourceId = lineNo1.getUpStationId(); // 인천
        long targetId = lineNo2.getUpStationId(); // 잠실

        ExtractableResponse<Response> response = RestApi.get(
            "paths?source=" + sourceId + "&target=" + targetId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("출발역과 도착역이 일치하면 Bad Request 상태를 반환한다.")
    @Test
    void showPaths_sameSourceAndTarget_statusBadRequest() {
        // given
        final LineWithStationId lineNo1 = createInitialLine("1호선", "인천", "부평");

        // when
        long stationId = lineNo1.getUpStationId();

        ExtractableResponse<Response> response = RestApi.get(
            "paths?source=" + stationId + "&target=" + stationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("출발역 혹은 도착역이 존재하지 않으면 Bad Request 상태를 반환한다. ")
    @Test
    void showPaths_notExistSourceOrTarget_statusBadRequest() {
        // given
        final long lastStationId = createStation("오류동");

        long notExistSourceId = lastStationId + 1;
        long notExistTargetId = lastStationId + 2;

        // when
        ExtractableResponse<Response> response = RestApi.get(
            "paths?source=" + notExistSourceId + "&target=" + notExistTargetId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("출발역과 도착역이 연결되어 있지 않으면 Bad Request 상태를 반환한다. ")
    @Test
    void showPaths_notConnectedSourceAndTarget_statusBadRequest() {
        // given
        final LineWithStationId lineNo1 = createInitialLine("1호선", "인천", "부평");
        final LineWithStationId lineNo2 = createInitialLine("2호선", "잠실", "잠실나루");

        // when
        long notConnectedSource = lineNo1.getUpStationId();
        long notConnectedTarget = lineNo2.getDownStationId();

        ExtractableResponse<Response> response = RestApi.get(
            "paths?source=" + notConnectedSource + "&target=" + notConnectedTarget);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
