package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.TestSettingUtils.createLineWith;
import static subway.integration.TestSettingUtils.createSectionWith;
import static subway.integration.TestSettingUtils.createStationWith;
import static subway.integration.TestSettingUtils.createStationsWithNames;
import static subway.integration.TestSettingUtils.extractCreatedId;
import static subway.integration.TestSettingUtils.extractCreatedIds;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.PathResponse;
import subway.dto.SectionAdditionRequest;
import subway.dto.StationRequest;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        //given
        final List<Long> stationIds = extractCreatedIds(
            createStationsWithNames("사당역", "방배역", "서초역"));
        final Long 사당역_ID = stationIds.get(0);
        final Long 방배역_ID = stationIds.get(1);
        final Long 서초역_ID = stationIds.get(2);

        final LineRequest lineRequest = new LineRequest("2호선", 사당역_ID, 방배역_ID, 3,
            "#ff0000");
        final Long lineId = extractCreatedId(createLineWith(lineRequest));
        final SectionAdditionRequest sectionAdditionRequest = new SectionAdditionRequest(방배역_ID,
            서초역_ID, 3);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .body(sectionAdditionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", lineId)
            .when()
            .post("/lines/{id}/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("지하철 노선의 구간을 제거한다.")
    @Test
    void removeSection() {
        //given
        final List<Long> stationIds = extractCreatedIds(
            createStationsWithNames("사당역", "방배역", "서초역"));
        final Long 사당역_ID = stationIds.get(0);
        final Long 방배역_ID = stationIds.get(1);
        final Long 서초역_ID = stationIds.get(2);

        final LineRequest lineRequest = new LineRequest("2호선", 사당역_ID, 방배역_ID, 3,
            "#ff0000");
        final Long lineId = extractCreatedId(createLineWith(lineRequest));

        final SectionAdditionRequest sectionAdditionRequest = new SectionAdditionRequest(방배역_ID,
            서초역_ID, 3);
        createSectionWith(sectionAdditionRequest, lineId);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", lineId)
            .param("stationId", 방배역_ID)
            .when().delete("/lines/{id}/sections")
            .then().log().all().
            extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("최단 경로를 구한다.")
    @Test
    void findShortestPath() {
        //given
        final Long 사당역_ID = extractCreatedId(createStationWith(new StationRequest("사당역")));
        final Long 방배역_ID = extractCreatedId(createStationWith(new StationRequest("방배역")));
        final Long 서초역_ID = extractCreatedId(createStationWith(new StationRequest("서초역")));
        final Long 교대역_ID = extractCreatedId(createStationWith(new StationRequest("교대역")));
        final Long 강남역_ID = extractCreatedId(createStationWith(new StationRequest("강남역")));

        final Long 이호선_ID = extractCreatedId(
            createLineWith(new LineRequest("2호선", 사당역_ID, 방배역_ID, 2, "green")));
        final Long 삼호선_ID = extractCreatedId(
            createLineWith(new LineRequest("3호선", 사당역_ID, 서초역_ID, 5, "orange")));

        createSectionWith(new SectionAdditionRequest(방배역_ID, 서초역_ID, 2), 이호선_ID);
        createSectionWith(new SectionAdditionRequest(서초역_ID, 교대역_ID, 2), 이호선_ID);
        createSectionWith(new SectionAdditionRequest(교대역_ID, 강남역_ID, 2), 이호선_ID);
        createSectionWith(new SectionAdditionRequest(서초역_ID, 강남역_ID, 3), 삼호선_ID);

        //when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .param("source", 사당역_ID)
            .param("target", 강남역_ID)
            .when()
            .get("/paths")
            .then().log().all()
            .extract();

        //then
        final PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getStations()).isNotEmpty();
        assertThat(pathResponse.getDistance()).isNotZero();
    }
}
