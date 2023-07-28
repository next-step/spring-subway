package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

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
import subway.RestApi;
import subway.ui.dto.LineRequest;
import subway.ui.dto.LineResponse;
import subway.ui.dto.SectionRequest;
import subway.ui.dto.StationRequest;
import subway.ui.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
class LineIntegrationTest extends IntegrationTest {

    private LineRequest lineRequest1;
    private LineRequest lineRequest2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lineRequest1 = new LineRequest("신분당선", 1L, 2L, 10, "bg-red-600");
        lineRequest2 = new LineRequest("구신분당선", 3L, 4L, 5, "bg-red-600");

        createInitialStations();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        // when
        ExtractableResponse<Response> response = createLine(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateNameTest() {
        // given
        createLine(lineRequest1);

        // when
        ExtractableResponse<Response> response = createLine(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLinesTest() {
        // given
        ExtractableResponse<Response> createResponse1 = createLine(lineRequest1);
        ExtractableResponse<Response> createResponse2 = createLine(lineRequest2);

        // when
        ExtractableResponse<Response> response = RestApi.get("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
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
        ExtractableResponse<Response> createResponse = createLine(lineRequest1);
        createInitialSections();

        List<StationResponse> stationResponses = createStationResponses();

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestApi.get("/lines/{lindId}", lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.as(LineResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(lineId);

        List<Long> expectedStationIds = extractIds(stationResponses);
        List<Long> resultLineIds = extractIds(resultResponse.getStations());
        assertThat(resultLineIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLineTest() {
        // given
        ExtractableResponse<Response> createResponse = createLine(lineRequest1);

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestApi.put(lineRequest2,
            "/lines/{lineId}", lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLineTest() {
        // given
        ExtractableResponse<Response> createResponse = createLine(lineRequest1);

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestApi.delete("/lines/{lineId}", lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void createInitialStations() {
        final StationRequest stationRequest1 = new StationRequest("오이도");
        final StationRequest stationRequest2 = new StationRequest("장지");
        final StationRequest stationRequest3 = new StationRequest("용산");
        final StationRequest stationRequest4 = new StationRequest("삼각지");

        RestApi.post(stationRequest1, "/stations");
        RestApi.post(stationRequest2, "/stations");
        RestApi.post(stationRequest3, "/stations");
        RestApi.post(stationRequest4, "/stations");
    }

    private void createInitialSections() {
        SectionRequest sectionRequest1 = new SectionRequest("2", "3", 10);
        SectionRequest sectionRequest2 = new SectionRequest("3", "4", 10);

        RestApi.post(sectionRequest1, "/lines/1/sections");
        RestApi.post(sectionRequest2, "/lines/1/sections");
    }

    private ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        return RestApi.post(lineRequest, "/lines");
    }

    private List<StationResponse> createStationResponses() {
        List<StationResponse> stationResponses = new ArrayList<>();
        stationResponses.add(StationResponse.of(1L, "오이도"));
        stationResponses.add(StationResponse.of(2L, "장지"));
        stationResponses.add(StationResponse.of(3L, "용산"));
        stationResponses.add(StationResponse.of(4L, "삼각지"));
        return stationResponses;
    }

    private List<Long> extractIds(List<StationResponse> stationResponses) {
        return stationResponses.stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
    }
}
