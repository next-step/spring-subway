package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static subway.exception.ErrorCode.NOT_CONNECTED_BETWEEN_START_AND_END_PATH;
import static subway.exception.ErrorCode.NOT_FOUND_END_PATH_POINT;
import static subway.exception.ErrorCode.NOT_FOUND_START_PATH_POINT;
import static subway.exception.ErrorCode.SAME_START_END_PATH_POINT;
import static subway.helper.TestHelper.createLine;
import static subway.helper.TestHelper.createSection;
import static subway.helper.TestHelper.createStation;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.LineRequest;
import subway.dto.PathResponse;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.exception.ErrorResponse;
import subway.helper.TestHelper;

public class PathIntegrationTest extends IntegrationTest {

    private Long station1Id;
    private Long station2Id;
    private Long station3Id;

    @BeforeEach
    public void setUp() {
        super.setUp();
        StationRequest stationRequest = new StationRequest("남부터미널");
        StationRequest stationRequest2 = new StationRequest("교대역");
        StationRequest stationRequest3 = new StationRequest("고속터미널");

        ExtractableResponse<Response> station1 = TestHelper.createStation(stationRequest);
        station1Id = Long.parseLong(station1.header("Location").split("/")[2]);
        ExtractableResponse<Response> station2 = TestHelper.createStation(stationRequest2);
        station2Id = Long.parseLong(station2.header("Location").split("/")[2]);
        ExtractableResponse<Response> station3 = TestHelper.createStation(stationRequest3);
        station3Id = Long.parseLong(station3.header("Location").split("/")[2]);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 15);
        ExtractableResponse<Response> lineResponse = createLine(lineRequest);
        Long lineId = Long.parseLong(lineResponse.header("Location").split("/")[2]);
        SectionRequest sectionRequest = new SectionRequest(station2Id, station3Id, 15);
        createSection(sectionRequest, lineId);
    }

    @Test
    @DisplayName("시작점과 도착점이 주어지면, 역과 최단 거리를 반환한다.")
    void 시작점_도착점_주어지면_역의_최단거리_반환() {
        // given
        long sourceId = station1Id;
        long targetId = station3Id;

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        PathResponse pathResponse = response.body().as(PathResponse.class);
        assertThat(pathResponse.getStations())
            .extracting("id").containsExactly(station1Id, station2Id, station3Id);
        assertThat(pathResponse).extracting("distance").isEqualTo(30L);
    }

    @Test
    @DisplayName("시작점과 도착점이 같다면, 오류를 반환한다.")
    void 시작점_도착점_같다면_오류_반환() {
        // given
        long sourceId = station1Id;
        long targetId = station1Id;

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getMessage())
            .isEqualTo(SAME_START_END_PATH_POINT.getMessage());
    }


    @Test
    @DisplayName("시작점과 도착점이 같다면, 오류를 반환한다.")
    void 시작점이_그래프에_없다면_오류_반환() {
        // given
        long targetId = station1Id;
        StationRequest stationRequest = new StationRequest("신촌역");
        ExtractableResponse<Response> stationResponse = createStation(stationRequest);
        long newSourceId = Long.parseLong(stationResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/paths?source={sourceId}&target={targetId}", newSourceId, targetId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
        assertThat(response.body().as(ErrorResponse.class).getMessage())
            .isEqualTo(NOT_FOUND_START_PATH_POINT.getMessage());
    }


    @Test
    @DisplayName("시작점과 도착점이 같다면, 오류를 반환한다.")
    void 도착점이_그래프에_없다면_오류_반환() {
        // given
        long sourceId = station1Id;
        StationRequest stationRequest = new StationRequest("신촌역");
        ExtractableResponse<Response> stationResponse = createStation(stationRequest);
        long newTargetId = Long.parseLong(stationResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/paths?source={sourceId}&target={targetId}", sourceId, newTargetId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
        assertThat(response.body().as(ErrorResponse.class).getMessage())
            .isEqualTo(NOT_FOUND_END_PATH_POINT.getMessage());
    }


    @Test
    @DisplayName("시작점과 도착점이 이어져 있지 않다면 오류 반환")
    void 시작점_도착점_연결_되지_않으면_오류_반환() {
        // given
        long sourceId = station1Id;

        StationRequest stationRequest = new StationRequest("신촌역");
        ExtractableResponse<Response> stationResponse = createStation(stationRequest);
        long newTargetId = Long.parseLong(stationResponse.header("Location").split("/")[2]);

        StationRequest stationRequest2 = new StationRequest("합정역");
        ExtractableResponse<Response> stationResponse2 = createStation(stationRequest2);
        long anotherStationId = Long.parseLong(stationResponse2.header("Location").split("/")[2]);

        LineRequest lineRequest = new LineRequest("3호선", "blue", newTargetId, anotherStationId, 15);
        createLine(lineRequest);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/paths?source={sourceId}&target={targetId}", sourceId, newTargetId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
        assertThat(response.body().as(ErrorResponse.class).getMessage())
            .isEqualTo(NOT_CONNECTED_BETWEEN_START_AND_END_PATH.getMessage());
    }

}
