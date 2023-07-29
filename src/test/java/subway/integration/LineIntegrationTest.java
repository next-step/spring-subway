package subway.integration;

import static subway.integration.HttpStatusAssertions.assertIsCreated;
import static subway.integration.HttpStatusAssertions.assertIsNoContent;
import static subway.integration.HttpStatusAssertions.assertIsOk;
import static subway.integration.LineIntegrationAssertions.assertIsCannotDisconnectSection;
import static subway.integration.LineIntegrationAssertions.assertIsDuplicateLineName;
import static subway.integration.LineIntegrationAssertions.assertIsDuplicateSection;
import static subway.integration.LineIntegrationAssertions.assertIsIllegalDistance;
import static subway.integration.LineIntegrationAssertions.assertIsLineCreated;
import static subway.integration.LineIntegrationAssertions.assertIsLineFound;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.LineCreateRequest;
import subway.dto.LineResponse;
import subway.dto.LineUpdateRequest;
import subway.dto.SectionCreateRequest;
import subway.dto.StationCreateRequest;
import subway.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
class LineIntegrationTest extends IntegrationTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = createLineByLineRequest(SinbundangLine.createRequest);

        // then
        assertIsLineCreated(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        createLineByLineRequest(SinbundangLine.createRequest);

        // when
        ExtractableResponse<Response> response = createLineByLineRequest(SinbundangLine.createRequest);

        // then
        assertIsDuplicateLineName(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        createLineByLineRequest(SinbundangLine.createRequest);
        createLineByLineRequest(SecondLine.createRequest);

        // when
        ExtractableResponse<Response> response = findAllLines();

        // then
        assertIsLineFound(response, 2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineByLineRequest(SinbundangLine.createRequest);

        long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = getLineByLineId(lineId);

        // then
        assertIsLineFound(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineByLineRequest(SinbundangLine.createRequest);
        long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        LineUpdateRequest lineUpdateRequest = new LineUpdateRequest("신분당선", "bg-red-600");

        // when
        ExtractableResponse<Response> response = updateLineByLineId(lineId, lineUpdateRequest);

        // then
        assertIsOk(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLineByLineRequest(SinbundangLine.createRequest);
        long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = deleteLineByLineId(lineId);

        // then
        assertIsNoContent(response);
    }

    @Test
    @DisplayName("Line에 구간을 등록한다.")
    void createSection() {
        // given
        long lineId = createLineByLineRequest(SinbundangLine.createRequest).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.bucheonStationId, 5);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, sectionCreateRequest);

        // then
        assertIsCreated(response);
    }

    @Test
    @DisplayName("Line의 상행 종점에 구간을 등록한다.")
    void createSectionOnLineUpStation() {
        // given
        long lineId = createLineByLineRequest(SecondLine.createRequest).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.bucheonStationId, 100);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, sectionCreateRequest);

        // then
        assertIsCreated(response);
    }

    @Test
    @DisplayName("Line의 중간에 삽입될 Section의 상행역을 기준으로 구간을 등록할 수 있다.")
    void createSectionOnLineMiddleStationByUpStation() {
        // given
        long lineId = createLineByLineRequest(SinbundangLine.createRequest).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.jamsilStationId, 10);
        registerSectionToLine(lineId, sectionCreateRequest);

        SectionCreateRequest middleSectionCreateRequest = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.bucheonStationId,
            2);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, middleSectionCreateRequest);

        // then
        assertIsCreated(response);
    }

    @Test
    @DisplayName("Line의 중간에 삽입될 Section의 상행역을 기준으로 구간을 등록할 수 있다.")
    void createSectionOnLineMiddleStationByDownStation() {
        // given
        long lineId = createLineByLineRequest(SinbundangLine.createRequest).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.jamsilStationId, 10);
        registerSectionToLine(lineId, sectionCreateRequest);

        SectionCreateRequest middleSectionCreateRequest = new SectionCreateRequest(SecondLine.bucheonStationId,
            SecondLine.jamsilStationId,
            2);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, middleSectionCreateRequest);

        // then
        assertIsCreated(response);
    }

    @Test
    @DisplayName("Line의 중간에 Line의 역 사이 길이보다 삽입될 Section의 길이가 더 크거나 같으면 삽입할 수 없다.")
    void cannotCreateSectionWhenSectionIsLongerThanSavedSectionDistance() {
        // given
        long lineId = createLineByLineRequest(SinbundangLine.createRequest).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.jamsilStationId, 5);
        registerSectionToLine(lineId, sectionCreateRequest);

        SectionCreateRequest middleSectionCreateRequest = new SectionCreateRequest(SecondLine.bucheonStationId,
            SecondLine.jamsilStationId,
            5);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, middleSectionCreateRequest);

        // then
        assertIsIllegalDistance(response);
    }

    @Test
    @DisplayName("Line에 Station 두개가 이미 존재할 경우, 구간을 삽입할 수 없다.")
    void cannotCreateSectionIfStationAlreadyExists() {
        // given
        long lineId = createLineByLineRequest(SinbundangLine.createRequest).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest1 = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.bucheonStationId, 100);
        registerSectionToLine(lineId, sectionCreateRequest1);

        SectionCreateRequest sectionCreateRequest2 = new SectionCreateRequest(SecondLine.bucheonStationId,
            SecondLine.jamsilStationId, 100);
        registerSectionToLine(lineId, sectionCreateRequest2);

        SectionCreateRequest sectionCreateRequest3 = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.jamsilStationId, 1);

        // when
        ExtractableResponse<Response> response = registerSectionToLine(lineId, sectionCreateRequest3);

        // then
        assertIsDuplicateSection(response);
    }

    @Test
    @DisplayName("Line의 하행 Station을 제거한다")
    void deleteDownSectionOfLine() {
        // given
        long lineId = createLineByLineRequest(SinbundangLine.createRequest).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.bucheonStationId, 5);

        registerSectionToLine(lineId, sectionCreateRequest);

        // when
        ExtractableResponse<Response> response = deleteSectionByLineIdAndStationId(lineId, SecondLine.bucheonStationId);

        // then
        assertIsOk(response);
    }

    @Test
    @DisplayName("Line의 중간 Station을 제거한다")
    void deleteMiddleStationOfLine() {
        // given
        long lineId = createLineByLineRequest(SinbundangLine.createRequest).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.bucheonStationId, 5);

        registerSectionToLine(lineId, sectionCreateRequest);

        // when
        ExtractableResponse<Response> response = deleteSectionByLineIdAndStationId(lineId,
            SinbundangLine.sindorimStationId);

        // then
        assertIsOk(response);
    }

    @Test
    @DisplayName("Line의 상행 Station을 제거한다")
    void deleteFirstStationOfLine() {
        // given
        long lineId = createLineByLineRequest(SinbundangLine.createRequest).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.bucheonStationId, 5);

        registerSectionToLine(lineId, sectionCreateRequest);

        // when
        ExtractableResponse<Response> response = deleteSectionByLineIdAndStationId(lineId,
            SinbundangLine.gangnamStationId);

        // then
        assertIsOk(response);
    }

    @Test
    @DisplayName("Line에 등록되어 있지 않은 Station을 제거하려고 하면, 400 에러가 응답된다")
    void deleteNotRegisteredStation() {
        // given
        long lineId = createLineByLineRequest(SinbundangLine.createRequest).body().as(LineResponse.class).getId();

        SectionCreateRequest sectionCreateRequest = new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SecondLine.bucheonStationId, 5);

        registerSectionToLine(lineId, sectionCreateRequest);

        // when
        ExtractableResponse<Response> response = deleteSectionByLineIdAndStationId(lineId, SecondLine.jamsilStationId);

        // then
        assertIsCannotDisconnectSection(response);
    }

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        SinbundangLine.gangnamStationId = createStation(new StationCreateRequest("강남")).body().as(StationResponse.class)
            .getId();
        SinbundangLine.sindorimStationId = createStation(new StationCreateRequest("신도림")).body()
            .as(StationResponse.class).getId();
        SecondLine.bucheonStationId = createStation(new StationCreateRequest("부천")).body().as(StationResponse.class)
            .getId();
        SecondLine.jamsilStationId = createStation(new StationCreateRequest("잠실")).body().as(StationResponse.class)
            .getId();

        SinbundangLine.createRequest = new LineCreateRequest("신분당선", "bg-red-600", SinbundangLine.gangnamStationId,
            SinbundangLine.sindorimStationId, 10);
        SecondLine.createRequest = new LineCreateRequest("2호선", "bg-green-600", SecondLine.bucheonStationId,
            SecondLine.jamsilStationId, 5);
    }

    private static final class SinbundangLine {

        private static LineCreateRequest createRequest;
        private static long gangnamStationId;
        private static long sindorimStationId;

    }

    private static final class SecondLine {

        private static LineCreateRequest createRequest;
        private static long bucheonStationId;
        private static long jamsilStationId;

    }

}
