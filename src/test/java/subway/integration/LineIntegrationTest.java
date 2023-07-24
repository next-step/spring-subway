package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
class LineIntegrationTest extends IntegrationTest {

    private LineRequest lineRequest1;
    private LineRequest lineRequest2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lineRequest1 = new LineRequest("신분당선", 1L, 2L, 10, "bg-red-600");
        lineRequest2 = new LineRequest("구신분당선", 3L, 4L, 5, "bg-red-600");

        createStations();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> response = LineIntegrationSupporter.createLine(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineIntegrationSupporter.createLine(lineRequest1);

        // when
        final ExtractableResponse<Response> response = LineIntegrationSupporter.createLine(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        final ExtractableResponse<Response> createResponse1 = LineIntegrationSupporter.createLine(lineRequest1);
        final ExtractableResponse<Response> createResponse2 = LineIntegrationSupporter.createLine(lineRequest2);

        // when
        final ExtractableResponse<Response> response = LineIntegrationSupporter.findAllLines();

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
        final ExtractableResponse<Response> createResponse = LineIntegrationSupporter.createLine(lineRequest1);

        LineIntegrationSupporter.createSectionInLine(1L, new SectionRequest("2", "3", 10));
        LineIntegrationSupporter.createSectionInLine(1L, new SectionRequest("3", "4", 10));

        final List<StationResponse> stationResponses = new ArrayList<>();
        stationResponses.add(StationResponse.of(1L, "오이도"));
        stationResponses.add(StationResponse.of(2L, "장지"));
        stationResponses.add(StationResponse.of(3L, "용산"));
        stationResponses.add(StationResponse.of(4L, "삼각지"));

        // when
        final Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        final ExtractableResponse<Response> response = LineIntegrationSupporter.findLine(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineWithStationsResponse resultResponse = response.as(LineWithStationsResponse.class);
        assertThat(resultResponse.getLineResponse().getId()).isEqualTo(lineId);

        assertThat(resultResponse.getStationResponses().get(0).getId()).isEqualTo(stationResponses.get(0).getId());
        assertThat(resultResponse.getStationResponses().get(1).getId()).isEqualTo(stationResponses.get(1).getId());
        assertThat(resultResponse.getStationResponses().get(2).getId()).isEqualTo(stationResponses.get(2).getId());
        assertThat(resultResponse.getStationResponses().get(3).getId()).isEqualTo(stationResponses.get(3).getId());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final ExtractableResponse<Response> createResponse = LineIntegrationSupporter.createLine(lineRequest1);

        // when
        final Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        final ExtractableResponse<Response> response = LineIntegrationSupporter.updateLine(lineId, lineRequest2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final ExtractableResponse<Response> createResponse = LineIntegrationSupporter.createLine(lineRequest1);

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = LineIntegrationSupporter.deleteLine(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void createStations() {
        final StationRequest stationRequest1 = new StationRequest("오이도");
        final StationRequest stationRequest2 = new StationRequest("장지");
        final StationRequest stationRequest3 = new StationRequest("용산");
        final StationRequest stationRequest4 = new StationRequest("삼각지");

        StationIntegrationSupporter.createStation(stationRequest1);
        StationIntegrationSupporter.createStation(stationRequest2);
        StationIntegrationSupporter.createStation(stationRequest3);
        StationIntegrationSupporter.createStation(stationRequest4);
    }
}
