package subway.integration;

import static subway.integration.LineIntegrationSupporter.createLineByLineRequest;
import static subway.integration.LineIntegrationSupporter.registerSectionToLine;
import static subway.integration.PathIntegrationAssertions.assertIsNotExistStation;
import static subway.integration.PathIntegrationAssertions.assertIsStationNotContainedPath;
import static subway.integration.PathIntegrationAssertions.assertStationPath;
import static subway.integration.PathIntegrationSupporter.findStationPath;
import static subway.integration.StationIntegrationSupporter.createStation;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.LineCreateRequest;
import subway.dto.LineResponse;
import subway.dto.PathFindResponse;
import subway.dto.SectionCreateRequest;
import subway.dto.StationCreateRequest;
import subway.dto.StationResponse;

@DisplayName("지하철 노선들의 경로 관련 기능")
class PathIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("하나의 노선에 포함된, 지하철 노선들의 경로를 조회할 수 있다.")
    void findStationPathInOneLine() {
        // given
        long sourceStationId = SinbundangLine.gangnamStationId;
        long targetStationId = SinbundangLine.yeokgokStationId;

        // when
        ExtractableResponse<Response> response = findStationPath(sourceStationId, targetStationId);

        // then
        assertStationPath(response, 15,
            SinbundangLine.expectedGangnamStationResponse,
            SinbundangLine.expectedSindorimStationResponse,
            SinbundangLine.expectedYeokgokStationResponse);
    }

    @Test
    @DisplayName("여러개의 노선에 포함된 지하철 노선들의 경로를 조회할 수 있다.")
    void findStationPathInManyLine() {
        // given
        long sourceStationId = SinbundangLine.gangnamStationId;
        long targetStationId = SecondLine.bucheonStationId;

        // when
        ExtractableResponse<Response> response = findStationPath(sourceStationId, targetStationId);

        // then
        assertStationPath(response, 25,
            SinbundangLine.expectedGangnamStationResponse,
            SinbundangLine.expectedSindorimStationResponse,
            SinbundangLine.expectedYeokgokStationResponse,
            SecondLine.expectedJamsilStationResponse,
            SecondLine.expectedBucheonStationResponse);
    }

    @Test
    @DisplayName("없는 station으로 요청하면, 400 BadRequest가 반환된다.")
    void returnBadRequestWhenCannotFindStation() {
        // given
        long notExistStationId = 999L;

        // when
        ExtractableResponse<Response> response = findStationPath(notExistStationId, notExistStationId);

        // then
        assertIsNotExistStation(response);
    }

    @Test
    @DisplayName("구간에 station이 포함되어 있지 않다면, 400 BadRequest가 반환된다.")
    void returnBadRequestWhenStationDoesNotContainedPath() {
        // given
        long notContainedStationId = createStation(new StationCreateRequest("포함되지 않음")).body().as(StationResponse.class)
            .getId();

        // when
        ExtractableResponse<Response> response = findStationPath(notContainedStationId,
            SinbundangLine.gangnamStationId);

        // then
        assertIsStationNotContainedPath(response);
    }

    private static final class SinbundangLine {

        private static long gangnamStationId;
        private static PathFindResponse.StationResponse expectedGangnamStationResponse;
        private static long sindorimStationId;
        private static PathFindResponse.StationResponse expectedSindorimStationResponse;
        private static long yeokgokStationId;
        private static PathFindResponse.StationResponse expectedYeokgokStationResponse;
        private static long id;

    }

    private static final class SecondLine {

        private static long bucheonStationId;
        private static PathFindResponse.StationResponse expectedBucheonStationResponse;
        private static long jamsilStationId;
        private static PathFindResponse.StationResponse expectedJamsilStationResponse;
        private static long id;

    }

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        SinbundangLine.gangnamStationId = createStation(new StationCreateRequest("강남")).body().as(StationResponse.class)
            .getId();
        SinbundangLine.expectedGangnamStationResponse = new PathFindResponse.StationResponse(
            SinbundangLine.gangnamStationId, "강남");

        SinbundangLine.sindorimStationId = createStation(new StationCreateRequest("신도림")).body()
            .as(StationResponse.class)
            .getId();
        SinbundangLine.expectedSindorimStationResponse = new PathFindResponse.StationResponse(
            SinbundangLine.sindorimStationId, "신도림");

        SinbundangLine.yeokgokStationId = createStation(new StationCreateRequest("역곡")).body().as(StationResponse.class)
            .getId();
        SinbundangLine.expectedYeokgokStationResponse = new PathFindResponse.StationResponse(
            SinbundangLine.yeokgokStationId, "역곡");

        SecondLine.bucheonStationId = createStation(new StationCreateRequest("부천")).body().as(StationResponse.class)
            .getId();
        SecondLine.expectedBucheonStationResponse = new PathFindResponse.StationResponse(
            SecondLine.bucheonStationId, "부천");

        SecondLine.jamsilStationId = createStation(new StationCreateRequest("잠실")).body().as(StationResponse.class)
            .getId();
        SecondLine.expectedJamsilStationResponse = new PathFindResponse.StationResponse(
            SecondLine.jamsilStationId, "잠실");

        SinbundangLine.id = createLineByLineRequest(
            new LineCreateRequest("신분당선", "bg-red-600", SinbundangLine.gangnamStationId,
                SinbundangLine.sindorimStationId, 10)).as(
            LineResponse.class).getId();
        registerSectionToLine(SinbundangLine.id, new SectionCreateRequest(SinbundangLine.sindorimStationId,
            SinbundangLine.yeokgokStationId, 5));

        SecondLine.id = createLineByLineRequest(
            new LineCreateRequest("2호선", "bg-green-600", SecondLine.bucheonStationId, SecondLine.jamsilStationId,
                5)).as(
            LineResponse.class).getId();

        registerSectionToLine(
            SecondLine.id, new SectionCreateRequest(SecondLine.jamsilStationId, SinbundangLine.yeokgokStationId, 5));
    }

}
