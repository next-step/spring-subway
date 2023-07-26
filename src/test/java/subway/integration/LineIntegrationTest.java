package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.request.LineCreateRequest;
import subway.dto.response.LineResponse;
import subway.dto.response.LineWithStationsResponse;
import subway.error.ErrorResponse;
import subway.integration.helper.LineIntegrationHelper;
import subway.integration.helper.StationIntegrationHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineIntegrationTest extends IntegrationTest {

    private LineCreateRequest lineCreateRequestA;
    private LineCreateRequest lineCreateRequestB;

    @BeforeEach
    public void setUp() {
        super.setUp();

        Station upStation = StationIntegrationHelper.createStation(Map.of("name", "낙성대"));
        Station downStation = StationIntegrationHelper.createStation(Map.of("name", "사당"));
        Station station = StationIntegrationHelper.createStation(Map.of("name", "방배"));

        lineCreateRequestA = new LineCreateRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10L);
        lineCreateRequestB = new LineCreateRequest("2호선", "bg-red-600", upStation.getId(), station.getId(), 10L);

    }

    @DisplayName("[POST] [/lines] 라인 이름 , 색깔 , 상행역 ,하행역 ID , 거리를 요청으로 지하철 노선을 생성하면서 첫 구간도 함께 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineCreateRequestA)
                .when().post("/lines")
                .then().log().all().
                extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("[POST] [/lines] 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성하면 BAD_REQUEST 웅답을 받는다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineIntegrationHelper.createLine(lineCreateRequestA);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineCreateRequestA)
                .when().post("/lines")
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("[GET] [/lines] 노선 ID로 지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        final Line lineA = LineIntegrationHelper.createLine(lineCreateRequestA);
        final Line lineB = LineIntegrationHelper.createLine(lineCreateRequestB);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).contains(lineA.getId(), lineB.getId());
    }

    @DisplayName("[GET] [/lines/{lineId}] 노선 ID로 지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final Line line = LineIntegrationHelper.createLine(lineCreateRequestA);

        // when
        Long lineId = line.getId();
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineWithStationsResponse resultResponse = response.as(LineWithStationsResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(lineId);
    }

    @DisplayName("[GET] [/lines/{lineId}] 없는 노선을 조회하면 BAD_REQUEST 웅답을 받는다.")
    @Test
    void notFoundLine() {
        // given
        final Line line = LineIntegrationHelper.createLine(lineCreateRequestA);

        // when
        Long lineId = line.getId();
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", -1L)
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.body().as(ErrorResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("노선을 찾을 수 없습니다.");
    }

    @DisplayName("[PUT] [/lines/{lineId}] 노선 ID 와 이름 , 색깔을 입력으로 지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final Line line = LineIntegrationHelper.createLine(lineCreateRequestA);

        // when
        Long lineId = line.getId();
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineCreateRequestB)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("[DELETE] [/lines/{lineId}] 노선 ID로 지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final Line line = LineIntegrationHelper.createLine(lineCreateRequestB);

        // when
        Long lineId = line.getId();
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
