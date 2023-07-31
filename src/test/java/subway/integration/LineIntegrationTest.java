package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.TestSettingUtils.createLineWith;
import static subway.integration.TestSettingUtils.createStationsWithNames;
import static subway.integration.TestSettingUtils.extractCreatedId;
import static subway.integration.TestSettingUtils.extractCreatedIds;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.LineResponse;

@DisplayName("지하철 노선 관련 기능")
class LineIntegrationTest extends IntegrationTest {

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        final List<Long> stationIds = extractCreatedIds(createStationsWithNames("사당역", "방배역"));
        final Long upStationId = stationIds.get(0);
        final Long downStationId = stationIds.get(1);
        final LineRequest lineRequest = new LineRequest("2호선", upStationId, downStationId, 3,
            "#ff0000");

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when().post("/lines")
            .then().log().all().
            extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("상행역과 하행역이 같으면 지하철 노선을 생성할 수 없다.")
    @Test
    void createLineWithSameUpAndDownStation() {
        //given
        final List<Long> stationIds = extractCreatedIds(createStationsWithNames("사당역"));
        final Long 사당역_ID = stationIds.get(0);
        final LineRequest lineRequest = new LineRequest("2호선", 사당역_ID, 사당역_ID, 3,
            "#ff0000");

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when().post("/lines")
            .then().log().all().
            extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 생성할 때 구간의 길이가 양수여야 한다.")
    @Test
    void createLineWithNonPositiveDistance() {
        //given
        final List<Long> stationIds = extractCreatedIds(createStationsWithNames("사당역", "방배역"));
        final Long upStationId = stationIds.get(0);
        final Long downStationId = stationIds.get(1);
        final LineRequest lineRequest = new LineRequest("2호선", upStationId, downStationId, -1,
            "#ff0000");

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when().post("/lines")
            .then().log().all().
            extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        //given
        final List<Long> stationIds = extractCreatedIds(createStationsWithNames("사당역", "방배역"));
        final Long upStationId = stationIds.get(0);
        final Long downStationId = stationIds.get(1);
        final LineRequest lineRequest = new LineRequest("2호선", upStationId, downStationId, 3,
            "#ff0000");
        createLineWith(lineRequest);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when().post("/lines")
            .then().log().all().
            extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        final List<Long> stationIds = extractCreatedIds(createStationsWithNames("사당역", "방배역"));
        final Long upStationId = stationIds.get(0);
        final Long downStationId = stationIds.get(1);
        final LineRequest lineRequest1 = new LineRequest("2호선", upStationId, downStationId, 3,
            "#ff0000");
        final LineRequest lineRequest2 = new LineRequest("5호선", upStationId, downStationId, 3,
            "#00ff00");

        final ExtractableResponse<Response> createResponse1 = createLineWith(lineRequest1);
        final ExtractableResponse<Response> createResponse2 = createLineWith(lineRequest2);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final List<Long> expectedLineIds = extractCreatedIds(
            List.of(createResponse1, createResponse2));
        final List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given
        final List<Long> stationIds = extractCreatedIds(createStationsWithNames("사당역", "방배역"));
        final Long upStationId = stationIds.get(0);
        final Long downStationId = stationIds.get(1);
        final LineRequest lineRequest = new LineRequest("2호선", upStationId, downStationId, 3,
            "#ff0000");
        final Long lineId = extractCreatedId(createLineWith(lineRequest));

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/lines/{lineId}", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.as(LineResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(lineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final List<Long> stationIds = extractCreatedIds(
            createStationsWithNames("사당역", "방배역", "서초역"));
        final Long upStationId = stationIds.get(0);
        final Long downStationId = stationIds.get(1);
        final Long newDownStationId = stationIds.get(2);
        final LineRequest lineRequest1 = new LineRequest("2호선", upStationId, downStationId, 3,
            "#ff0000");
        final LineRequest lineRequest2 = new LineRequest("3호선", upStationId, newDownStationId, 6,
            "#00ff00");
        final Long lineId = extractCreatedId(createLineWith(lineRequest1));

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest2)
            .when().put("/lines/{lineId}", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final List<Long> stationIds = extractCreatedIds(createStationsWithNames("사당역", "방배역"));
        final Long upStationId = stationIds.get(0);
        final Long downStationId = stationIds.get(1);
        final LineRequest lineRequest = new LineRequest("2호선", upStationId, downStationId, 3,
            "#ff0000");
        final Long lineId = extractCreatedId(createLineWith(lineRequest));

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().delete("/lines/{lineId}", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
