package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.ExceptionResponse;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineUpdateRequest;
import subway.dto.StationRequest;
import subway.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.util.TestRequestUtil.createLine;
import static subway.util.TestRequestUtil.createStation;
import static subway.util.TestRequestUtil.extractId;

@DisplayName("지하철 노선 관련 기능 통합 테스트")
public class LineIntegrationTest extends IntegrationTest {
    private Long station1Id;
    private Long station2Id;

    @BeforeEach
    public void setUp() {
        super.setUp();

        StationRequest stationRequest1 = new StationRequest("신대방역");
        StationRequest stationRequest2 = new StationRequest("서울대입구역");

        ExtractableResponse<Response> createStation1Response = createStation(stationRequest1);
        station1Id = extractId(createStation1Response);

        ExtractableResponse<Response> createStation2Response = createStation(stationRequest2);
        station2Id = extractId(createStation2Response);
    }

    @Test
    @DisplayName("지하철 노선을 생성한다.")
    void createLineTest() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 14);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("존재하지 않는 상행종점역으로 지하철 노선을 생성할 수 없다.")
    void createLineWithNonExistUpStationTest() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", 5L, station2Id, 14);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.UP_STATION_ID_NO_EXIST.getMessage() + "5");
    }

    @Test
    @DisplayName("존재하지 않는 하행종점역으로 지하철 노선을 생성할 수 없다.")
    void createLineWithNonExistDownStationTest() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, 5L, 14);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.DOWN_STATION_ID_NO_EXIST.getMessage() + "5");
    }

    @Test
    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성할 수 없다.")
    void createLineWithDuplicateName() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 14);
        createLine(lineRequest);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.LINE_NAME_DUPLICATE.getMessage() + "2호선");
    }

    @Test
    @DisplayName("노선의 두 역이 같으면, 노선을 생성할 수 없다.")
    void createLineWithDuplicateStation() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", station2Id, station2Id, 14);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.SECTION_SAME_STATIONS.getMessage());
    }

    @Test
    @DisplayName("지하철 노선 목록을 조회한다.")
    void getLines() {
        // given
        LineRequest lineRequest1 = new LineRequest("2호선", "green", station1Id, station2Id, 14);
        ExtractableResponse<Response> createResponse1 = createLine(lineRequest1);

        LineRequest lineRequest2 = new LineRequest("3호선", "orange", station1Id, station2Id, 15);
        ExtractableResponse<Response> createResponse2 = createLine(lineRequest2);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> extractId(it))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @Test
    @DisplayName("지하철 노선을 조회한다.")
    void getLine() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 14);
        ExtractableResponse<Response> createResponse = createLine(lineRequest);

        // when
        Long lineId = extractId(createResponse);
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

    @Test
    @DisplayName("존재하지 않는 id로 지하철 노선을 조회한다.")
    void getLineNonExist() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 14);
        ExtractableResponse<Response> createResponse = createLine(lineRequest);

        // when
        Long lineId = extractId(createResponse);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", 5L)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.LINE_ID_NO_EXIST.getMessage() + "5");
    }

    @Test
    @DisplayName("지하철 노선을 수정한다.")
    void updateLine() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 14);
        ExtractableResponse<Response> createResponse = createLine(lineRequest);

        // when
        Long lineId = extractId(createResponse);

        LineUpdateRequest updateRequest = new LineUpdateRequest("구신분당선", "bg-red-600");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updateRequest)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("기존에 존재하는 노선 이름으로 지하철 노선을 수정할 수 없다.")
    void updateLineDuplicateName() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 14);
        ExtractableResponse<Response> createResponse = createLine(lineRequest);

        LineRequest lineRequest2 = new LineRequest("4호선", "green", station1Id, station2Id, 14);
        createLine(lineRequest2);

        // when
        Long lineId = extractId(createResponse);

        LineUpdateRequest updateRequest = new LineUpdateRequest("4호선", "bg-red-600");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updateRequest)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.LINE_NAME_DUPLICATE.getMessage() + "4호선");
    }

    @Test
    @DisplayName("지하철 노선을 제거한다.")
    void deleteLine() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 14);
        ExtractableResponse<Response> createResponse = createLine(lineRequest);

        // when
        Long lineId = extractId(createResponse);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
