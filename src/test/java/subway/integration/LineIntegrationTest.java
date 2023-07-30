package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.DomainFixtures.createInitialLine;
import static subway.DomainFixtures.createStation;
import static subway.DomainFixtures.extendSectionToLine;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.DomainFixtures;
import subway.DomainFixtures.LineWithStationId;
import subway.RestApiUtils;
import subway.domain.Station;
import subway.ui.dto.LineRequest;
import subway.ui.dto.LineResponse;
import subway.ui.dto.SectionRequest;
import subway.ui.dto.StationRequest;
import subway.ui.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
class LineIntegrationTest extends IntegrationTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        // when
        ExtractableResponse<Response> response = createLine("신분당선", "판교", "정자");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateNameTest() {
        // given
        String duplicateLineName = "신분당선";
        createLine(duplicateLineName, "판교", "정자");

        // when
        ExtractableResponse<Response> response = createLine(duplicateLineName, "광교", "신사");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLinesTest() {
        // given
        ExtractableResponse<Response> lineNo1 = createLine("1호선", "판교", "정");
        ExtractableResponse<Response> lineNo2 = createLine("2호선", "오리", "미금");

        // when
        ExtractableResponse<Response> response = RestApiUtils.get("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(lineNo1, lineNo2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLineTest() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "판교", "정자");
        long newStationId = createStation("광교");
        extendSectionToLine(lineNo1.getLineId(), lineNo1.getDownStationId(), newStationId);
        List<Long> expectedStationIds = List.of(lineNo1.getUpStationId(),
            lineNo1.getDownStationId(), newStationId);

        // when
        Long lineId = lineNo1.getLineId();
        ExtractableResponse<Response> response = RestApiUtils.get("/lines/{lindId}", lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.as(LineResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(lineId);

        List<Long> resultLineIds = extractIds(resultResponse.getStations());
        assertThat(resultLineIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLineTest() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "인천", "부평");

        // when
        LineRequest updateRequest = new LineRequest("신분당선", lineNo1.getUpStationId(),
            lineNo1.getDownStationId(), 10, "red");
        ExtractableResponse<Response> response = RestApiUtils.put(updateRequest,
            "/lines/{lineId}", lineNo1.getLineId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLineTest() {
        // given
        LineWithStationId lineNo1 = createInitialLine("1호선", "부평", "인천");

        // when
        long lineId = lineNo1.getLineId();
        ExtractableResponse<Response> response = RestApiUtils.delete("/lines/{lineId}", lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLine(String name, String upStationName,
        String downStationName) {
        long upStationId = createStation(upStationName);
        long downStationId = createStation(downStationName);
        LineRequest lineRequest = new LineRequest(name, upStationId, downStationId, 10,
            "bg-red-600");
        return RestApiUtils.post(lineRequest, "/lines");
    }

    private List<Long> extractIds(List<StationResponse> stationResponses) {
        return stationResponses.stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
    }
}
