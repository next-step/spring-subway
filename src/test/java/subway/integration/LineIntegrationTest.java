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
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

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
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestApi.post(lineRequest1, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        RestApi.post(lineRequest1, "/lines");

        // when
        ExtractableResponse<Response> response = RestApi.post(lineRequest1, "/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = RestApi.post(lineRequest1, "/lines");
        ExtractableResponse<Response> createResponse2 = RestApi.post(lineRequest2, "/lines");

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
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = RestApi.post(lineRequest1, "/lines");
        RestApi.post(new SectionRequest("2", "3", 10),
            "/lines/1/sections");
        RestApi.post(new SectionRequest("3", "4", 10),
            "/lines/1/sections");

        List<StationResponse> stationResponses = new ArrayList<>();
        stationResponses.add(StationResponse.of(1L, "오이도"));
        stationResponses.add(StationResponse.of(2L, "장지"));
        stationResponses.add(StationResponse.of(3L, "용산"));
        stationResponses.add(StationResponse.of(4L, "삼각지"));

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestApi.get("/lines/{lindId}", lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.as(LineResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(lineId);

        assertThat(resultResponse.getStations().get(0).getId()).isEqualTo(
            stationResponses.get(0).getId());
        assertThat(resultResponse.getStations().get(1).getId()).isEqualTo(
            stationResponses.get(1).getId());
        assertThat(resultResponse.getStations().get(2).getId()).isEqualTo(
            stationResponses.get(2).getId());
        assertThat(resultResponse.getStations().get(3).getId()).isEqualTo(
            stationResponses.get(3).getId());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = RestApi.post(lineRequest1,
            "/lines");

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestApi.put(lineRequest2,
            "/lines/{lineId}", lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = RestApi.post(lineRequest1,
            "/lines");

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestApi.delete("/lines/{lineId}",
            lineId);

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
}
