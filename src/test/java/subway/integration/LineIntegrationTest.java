package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.LineIntegrationSupporter.createLineByLineRequest;
import static subway.integration.LineIntegrationSupporter.deleteLineByLineId;
import static subway.integration.LineIntegrationSupporter.deleteSectionByLineIdAndStationId;
import static subway.integration.LineIntegrationSupporter.findAllLines;
import static subway.integration.LineIntegrationSupporter.getLineByLineId;
import static subway.integration.LineIntegrationSupporter.registerSectionToLine;
import static subway.integration.LineIntegrationSupporter.updateLineByLineId;
import static subway.integration.StationIntegrationSupporter.createStation;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.LineCreateRequest;
import subway.dto.LineResponse;
import subway.dto.LineUpdateRequest;
import subway.dto.SectionCreateRequest;
import subway.dto.StationCreateRequest;
import subway.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
class LineIntegrationTest extends IntegrationTest {

    private long stationRequest1;
    private long stationRequest2;
    private long stationRequest3;
    private long stationRequest4;
    private LineCreateRequest lineCreateRequest1;
    private LineCreateRequest lineCreateRequest2;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        stationRequest1 = createStation(new StationCreateRequest("강남")).body().as(StationResponse.class).getId();
        stationRequest2 = createStation(new StationCreateRequest("신도림")).body().as(StationResponse.class).getId();
        stationRequest3 = createStation(new StationCreateRequest("부천")).body().as(StationResponse.class).getId();
        stationRequest4 = createStation(new StationCreateRequest("잠실")).body().as(StationResponse.class).getId();

        lineCreateRequest1 = new LineCreateRequest("신분당선", "bg-red-600", stationRequest1, stationRequest2, 10);

        lineCreateRequest2 = new LineCreateRequest("2호선", "bg-green-600", stationRequest3, stationRequest4, 5);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = createLineByLineRequest(lineCreateRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        createLineByLineRequest(lineCreateRequest1);

        // when
        ExtractableResponse<Response> response = createLineByLineRequest(lineCreateRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = createLineByLineRequest(lineCreateRequest1);
        ExtractableResponse<Response> createResponse2 = createLineByLineRequest(lineCreateRequest2);

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
        ExtractableResponse<Response> createResponse = createLineByLineRequest(lineCreateRequest1);

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
        ExtractableResponse<Response> createResponse = createLineByLineRequest(lineCreateRequest1);
        Long lineId = Long.valueOf(createResponse.header("Location").split("/")[2]);
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = updateLineByLineId(lineId, lineUpdateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineByLineRequest(lineCreateRequest1);
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
        Long lineId = createLineByLineRequest(lineCreateRequest1).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(stationRequest2, stationRequest3, 5);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, sectionCreateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Line의 상행 종점에 구간을 등록한다.")
    void createSectionOnLineUpStation() {
        // given
        Long lineId = createLineByLineRequest(lineCreateRequest2).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(stationRequest2, stationRequest3, 100);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, sectionCreateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Line의 중간에 삽입될 Section의 상행역을 기준으로 구간을 등록할 수 있다.")
    void createSectionOnLineMiddleStationByUpStation() {
        // given
        Long lineId = createLineByLineRequest(lineCreateRequest1).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(stationRequest2, stationRequest4, 10);
        registerSectionToLine(lineId, sectionCreateRequest);

        SectionCreateRequest middleSectionCreateRequest = new SectionCreateRequest(stationRequest2, stationRequest3, 2);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, middleSectionCreateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Line의 중간에 삽입될 Section의 상행역을 기준으로 구간을 등록할 수 있다.")
    void createSectionOnLineMiddleStationByDownStation() {
        // given
        Long lineId = createLineByLineRequest(lineCreateRequest1).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(stationRequest2, stationRequest4, 10);
        registerSectionToLine(lineId, sectionCreateRequest);

        SectionCreateRequest middleSectionCreateRequest = new SectionCreateRequest(stationRequest3, stationRequest4, 2);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, middleSectionCreateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("Line의 중간에 Line의 역 사이 길이보다 삽입될 Section의 길이가 더 크거나 같으면 삽입할 수 없다.")
    void cannotCreateSectionWhenSectionIsLongerThanSavedSectionDistance() {
        // given
        Long lineId = createLineByLineRequest(lineCreateRequest1).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(stationRequest2, stationRequest4, 5);
        registerSectionToLine(lineId, sectionCreateRequest);

        SectionCreateRequest middleSectionCreateRequest = new SectionCreateRequest(stationRequest3, stationRequest4, 5);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, middleSectionCreateRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Line에 Station 두개가 이미 존재할 경우, 구간을 삽입할 수 없다.")
    void cannotCreateSectionIfStationAlreadyExists() {
        // given
        Long lineId = createLineByLineRequest(lineCreateRequest1).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest1 = new SectionCreateRequest(stationRequest2, stationRequest3, 100);
        registerSectionToLine(lineId, sectionCreateRequest1);

        SectionCreateRequest sectionCreateRequest2 = new SectionCreateRequest(stationRequest3, stationRequest4, 100);
        registerSectionToLine(lineId, sectionCreateRequest2);

        SectionCreateRequest sectionCreateRequest3 = new SectionCreateRequest(stationRequest2, stationRequest4, 1);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, sectionCreateRequest3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Line의 하행 Station을 제거한다")
    void deleteDownSectionOfLine() {
        // given
        Long lineId = createLineByLineRequest(lineCreateRequest1).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(stationRequest2, stationRequest3, 5);

        registerSectionToLine(lineId, sectionCreateRequest);

        // when
        ExtractableResponse<Response> response = deleteSectionByLineIdAndStationId(lineId, stationRequest3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Line의 중간 Station을 제거한다")
    void deleteMiddleStationOfLine() {
        // given
        Long lineId = createLineByLineRequest(lineCreateRequest1).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(stationRequest2, stationRequest3, 5);

        registerSectionToLine(lineId, sectionCreateRequest);

        // when
        ExtractableResponse<Response> response = deleteSectionByLineIdAndStationId(lineId, stationRequest2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
