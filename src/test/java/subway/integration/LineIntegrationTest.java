package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.request.LineCreationRequest;
import subway.dto.response.LineResponse;
import subway.integration.helper.SubWayHelper;

@DisplayName("지하철 노선 관련 기능")
class LineIntegrationTest extends IntegrationTest {
    LineCreationRequest lineCreationRequest1;
    LineCreationRequest lineCreationRequest2;

    Line lineA;
    Station stationA;
    Station stationB;

    @BeforeEach
    public void setUp() {
        super.setUp();
        lineA = new Line(1L, "A", "red");
        stationA = new Station(1L, "A");
        stationB = new Station(2L, "B");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        StationIntegrationTest.createInitialStations();
        lineCreationRequest1 = new LineCreationRequest("신분당선", 1L, 2L, 3, "bg-red-600");

        // when
        ExtractableResponse<Response> response = SubWayHelper.createLine(lineCreationRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        StationIntegrationTest.createInitialStations();
        lineCreationRequest1 = new LineCreationRequest("신분당선", 1L, 2L, 3, "bg-red-600");
        SubWayHelper.createLine(lineCreationRequest1);

        // when
        ExtractableResponse<Response> response = SubWayHelper.createLine(lineCreationRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationIntegrationTest.createInitialStations();
        lineCreationRequest1 = new LineCreationRequest("신분당선", 1L, 2L, 3, "bg-red-600");
        lineCreationRequest2 = new LineCreationRequest("경강선", 1L, 2L, 3, "bg-red-600");
        ExtractableResponse<Response> createResponse1 = SubWayHelper.createLine(lineCreationRequest1);
        ExtractableResponse<Response> createResponse2 = SubWayHelper.createLine(lineCreationRequest2);

        // when
        ExtractableResponse<Response> response = SubWayHelper.findAllLines();

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
        StationIntegrationTest.createInitialStations();
        lineCreationRequest1 = new LineCreationRequest("신분당선", 1L, 2L, 3, "bg-red-600");
        ExtractableResponse<Response> createResponse = SubWayHelper.createLine(lineCreationRequest1);

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = SubWayHelper.findLine(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.as(LineResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(lineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationIntegrationTest.createInitialStations();
        lineCreationRequest1 = new LineCreationRequest("신분당선", 1L, 2L, 3, "bg-red-600");
        lineCreationRequest2 = new LineCreationRequest("경강선", 1L, 2L, 3, "bg-red-600");
        ExtractableResponse<Response> createResponse = SubWayHelper.createLine(lineCreationRequest1);

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = SubWayHelper.updateLine(lineId, lineCreationRequest2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationIntegrationTest.createInitialStations();
        lineCreationRequest1 = new LineCreationRequest("신분당선", 1L, 2L, 3, "bg-red-600");
        ExtractableResponse<Response> createResponse = SubWayHelper.createLine(lineCreationRequest1);

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = SubWayHelper.deleteLine(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
