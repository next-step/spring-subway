package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.RestApi;
import subway.ui.dto.LineRequest;
import subway.ui.dto.SectionRequest;
import subway.ui.dto.StationRequest;

@DisplayName("지하철 구간 조회 관련 기능")
class PathIntegrationTest extends IntegrationTest {

    @DisplayName("최단 경로와 거리를 조회하면 OK 상태를 반환한다.")
    @Test
    void showPaths_returnStatusOK() {
        // given
        long sourceId = 3;
        long targetId = 5;
        createInitialSections();

        // when
        ExtractableResponse<Response> response = RestApi.get(
            "paths?source=" + sourceId + "&target=" + targetId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("출발역과 도착역이 일치하면 Bad Request 상태를 반환한다.")
    @Test
    void showPaths_sameSourceAndTarget_statusBadRequest() {
        // given
        long sourceId = 3;
        long targetId = 3;
        createInitialSections();

        // when
        ExtractableResponse<Response> response = RestApi.get(
            "paths?source=" + sourceId + "&target=" + targetId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("출발역 혹은 도착역이 존재하지 않으면 Bad Request 상태를 반환한다. ")
    @Test
    void showPaths_notExistSourceOrTarget_statusBadRequest() {
        // given
        long notExistSourceId = 10;
        long notExistTargetId = 11;
        createInitialSections();

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
        long notConnectedSource = 1;
        long notConnectedTarget = 3;
        createInitialSections();

        // when
        ExtractableResponse<Response> response = RestApi.get(
            "paths?source=" + notConnectedSource + "&target=" + notConnectedTarget);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private long createLine(String name, long upStationId, long downStationId) {
        final LineRequest line = new LineRequest(name, upStationId, downStationId, 10, "blue");
        return extractIdFromApiResult(RestApi.post(line, "/lines"));
    }

    private long createStation(String name) {
        final StationRequest stationRequest = new StationRequest(name);
        return extractIdFromApiResult(RestApi.post(stationRequest, "/stations"));
    }

    private long createInitialLine(String name, String upStationName,
        String downStationName) {
        long upStationId = createStation(upStationName);
        long downStationId = createStation(downStationName);
        return createLine(name, upStationId, downStationId);
    }

    private void extendSectionToLine(long lineId, long upStationId, long downStationId) {
        final SectionRequest extendToDownStation = new SectionRequest(
            String.valueOf(upStationId),
            String.valueOf(downStationId),
            10
        );

        RestApi.post(extendToDownStation, "/lines/" + lineId + "/sections");
    }

    private void createInitialSections() {
        createInitialLine("3호선", "대화", "구파발");
        final long lineNo1Id = createInitialLine("1호선", "인천", "부평");
        final long lineNo2Id = createInitialLine("2호선", "잠실", "잠실나루");
        final long stationId = createStation("신도림");

        /**
         *  대화                           잠실
         *   |                            |
         * *3호선*                       *2호선*
         *  |                            |
         * 구파발                       잠실나루
         *                              |
         * 인천--- *1호선* ---부평--- ---신도림
         */
        extendSectionToLine(lineNo1Id, 4, stationId);
        extendSectionToLine(lineNo2Id, 6, stationId);
    }

    private long extractIdFromApiResult(ExtractableResponse<Response> apiResponse) {
        RestAssured.defaultParser = Parser.JSON;
        return apiResponse.jsonPath().getLong("id");
    }
}
