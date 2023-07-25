package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.CreateSectionRequest;
import subway.dto.LineResponse;
import subway.dto.StationResponse;
import subway.dto.UpdateLineRequest;
import subway.dto.UpdateStationRequest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.LineIntegrationSupporter.createLineByLineRequest;
import static subway.integration.LineIntegrationSupporter.deleteLineByLineId;
import static subway.integration.LineIntegrationSupporter.deleteSectionByLineIdAndStationId;
import static subway.integration.LineIntegrationSupporter.findAllLines;
import static subway.integration.LineIntegrationSupporter.getLineByLineId;
import static subway.integration.LineIntegrationSupporter.registerSectionToLine;
import static subway.integration.LineIntegrationSupporter.updateLineByLineId;
import static subway.integration.StationIntegrationSupporter.createStation;

@DisplayName("지하철 노선 관련 기능")
class LineIntegrationTest extends IntegrationTest {

    private Long stationRequest1;
    private Long stationRequest2;
    private Long stationRequest3;
    private Long stationRequest4;

    private UpdateLineRequest lineRequest1;
    private UpdateLineRequest lineRequest2;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        stationRequest1 = createStation(new UpdateStationRequest("역1")).body().as(StationResponse.class).getId();
        stationRequest2 = createStation(new UpdateStationRequest("역2")).body().as(StationResponse.class).getId();
        stationRequest3 = createStation(new UpdateStationRequest("역3")).body().as(StationResponse.class).getId();
        stationRequest4 = createStation(new UpdateStationRequest("역4")).body().as(StationResponse.class).getId();

        lineRequest1 = new UpdateLineRequest("노선1", "bg-red-600", stationRequest1, stationRequest2, 10);
        lineRequest2 = new UpdateLineRequest("노선2", "bg-green-600", stationRequest2, stationRequest3, 5);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = createLineByLineRequest(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        createLineByLineRequest(lineRequest1);

        // when
        ExtractableResponse<Response> response = createLineByLineRequest(lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = createLineByLineRequest(lineRequest1);
        ExtractableResponse<Response> createResponse2 = createLineByLineRequest(lineRequest2);

        // when
        ExtractableResponse<Response> response = findAllLines();

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.valueOf(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineByLineRequest(lineRequest1);

        Long lineId = Long.valueOf(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = getLineByLineId(lineId);
        LineResponse resultResponse = response.as(LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultResponse.getId()).isEqualTo(lineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineByLineRequest(lineRequest1);
        Long lineId = Long.valueOf(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = updateLineByLineId(lineId, lineRequest2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineByLineRequest(lineRequest1);
        Long lineId = Long.valueOf(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = deleteLineByLineId(lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("Line에 구간을 등록한다.")
    void createSection() {
        // given
        Long lineId = createLineByLineRequest(lineRequest1).body().as(LineResponse.class).getId();

        CreateSectionRequest sectionRequest = new CreateSectionRequest(stationRequest2, stationRequest3, 5);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Line의 상행 종점에 구간을 등록한다.")
    void createSectionOnLineUpStation() {
        // given
        Long lineId = createLineByLineRequest(lineRequest2).body().as(LineResponse.class).getId();

        CreateSectionRequest sectionRequest = new CreateSectionRequest(stationRequest1, stationRequest2, 100);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, sectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Line의 중간에 삽입될 Section의 상행역을 기준으로 구간을 등록할 수 있다.")
    void createSectionOnLineMiddleStationByUpStation() {
        // given
        Long lineId = createLineByLineRequest(lineRequest1).body().as(LineResponse.class).getId();

        CreateSectionRequest sectionRequest = new CreateSectionRequest(stationRequest2, stationRequest4, 10);
        registerSectionToLine(lineId, sectionRequest);

        CreateSectionRequest middleSectionRequest = new CreateSectionRequest(stationRequest2, stationRequest3, 2);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, middleSectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Line의 중간에 삽입될 Section의 하행역을 기준으로 구간을 등록할 수 있다.")
    void createSectionOnLineMiddleStationByDownStation() {
        // given
        Long lineId = createLineByLineRequest(lineRequest1).body().as(LineResponse.class).getId();

        CreateSectionRequest sectionRequest = new CreateSectionRequest(stationRequest2, stationRequest4, 10);
        registerSectionToLine(lineId, sectionRequest);

        CreateSectionRequest middleSectionRequest = new CreateSectionRequest(stationRequest3, stationRequest4, 2);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, middleSectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Line의 중간에 Line의 역 사이 길이보다 삽입될 Section의 길이가 더 크거나 같으면 삽입할 수 없다.")
    void cannotCreateSectionWhenSectionIsLongerThanSavedSectionDistance() {
        // given
        Long lineId = createLineByLineRequest(lineRequest1).body().as(LineResponse.class).getId();

        CreateSectionRequest sectionRequest = new CreateSectionRequest(stationRequest2, stationRequest4, 5);
        registerSectionToLine(lineId, sectionRequest);

        CreateSectionRequest middleSectionRequest = new CreateSectionRequest(stationRequest3, stationRequest4, 5);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, middleSectionRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Line에 Station 두개가 이미 존재할 경우, 구간을 삽입할 수 없다.")
    void cannotCreateSectionIfStationAlreadyExists() {
        // given
        Long lineId = createLineByLineRequest(lineRequest1).body().as(LineResponse.class).getId();

        CreateSectionRequest sectionRequest1 = new CreateSectionRequest(stationRequest2, stationRequest3, 100);
        registerSectionToLine(lineId, sectionRequest1);

        CreateSectionRequest sectionRequest2 = new CreateSectionRequest(stationRequest3, stationRequest4, 100);
        registerSectionToLine(lineId, sectionRequest2);

        CreateSectionRequest sectionRequest3 = new CreateSectionRequest(stationRequest2, stationRequest4, 1);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, sectionRequest3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Line의 하행 Section을 제거한다")
    void deleteDownSectionOfLine() {
        // given
        Long lineId = createLineByLineRequest(lineRequest1).body().as(LineResponse.class).getId();

        CreateSectionRequest sectionRequest = new CreateSectionRequest(stationRequest2, stationRequest3, 5);

        registerSectionToLine(lineId, sectionRequest);

        // when
        ExtractableResponse<Response> response = deleteSectionByLineIdAndStationId(lineId, stationRequest3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
