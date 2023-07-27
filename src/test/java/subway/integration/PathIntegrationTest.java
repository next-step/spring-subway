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
import subway.dto.SectionCreateRequest;
import subway.dto.StationCreateRequest;
import subway.dto.StationResponse;

@DisplayName("지하철 노선들의 경로 관련 기능")
class PathIntegrationTest extends IntegrationTest {


    private long station1Request1;
    private long station1Request2;
    private long station1Request3;
    private long station2Request1;
    private long station2Request2;
    private long createdLine1;
    private long createdLine2;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        station1Request1 = createStation(new StationCreateRequest("강남")).body().as(StationResponse.class).getId();
        station1Request2 = createStation(new StationCreateRequest("신도림")).body().as(StationResponse.class).getId();
        station1Request3 = createStation(new StationCreateRequest("역곡")).body().as(StationResponse.class).getId();
        station2Request1 = createStation(new StationCreateRequest("부천")).body().as(StationResponse.class).getId();
        station2Request2 = createStation(new StationCreateRequest("잠실")).body().as(StationResponse.class).getId();

        createdLine1 = createLineByLineRequest(
                new LineCreateRequest("신분당선", "bg-red-600", station1Request1, station1Request2, 10)).as(
                LineResponse.class).getId();
        registerSectionToLine(createdLine1, new SectionCreateRequest(station1Request2, station1Request3, 5));

        createdLine2 = createLineByLineRequest(
                new LineCreateRequest("2호선", "bg-green-600", station2Request1, station2Request2, 5)).as(
                LineResponse.class).getId();
        registerSectionToLine(createdLine2, new SectionCreateRequest(station2Request2, station1Request3, 5));
    }

    @Test
    @DisplayName("하나의 노선에 포함된, 지하철 노선들의 경로를 조회할 수 있다.")
    void findStationPathInOneLine() {
        // given
        long sourceStationId = station1Request1;
        long targetStationId = station1Request3;

        // when
        ExtractableResponse<Response> response = findStationPath(sourceStationId, targetStationId);

        // then
        assertStationPath(response, 3);
    }

    @Test
    @DisplayName("여러개의 노선에 포함된 지하철 노선들의 경로를 조회할 수 있다.")
    void findStationPathInManyLine() {
        // given
        long sourceStationId = station1Request1;
        long targetStationId = station2Request1;

        // when
        ExtractableResponse<Response> response = findStationPath(sourceStationId, targetStationId);

        // then
        assertStationPath(response, 5);
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
        ExtractableResponse<Response> response = findStationPath(notContainedStationId, station1Request1);

        // then
        assertIsStationNotContainedPath(response);
    }

}
