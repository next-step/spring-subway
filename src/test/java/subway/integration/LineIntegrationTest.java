package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineStationsResponse;
import subway.helper.CreateHelper;
import subway.helper.RestAssuredHelper;

@DisplayName("지하철 노선 관련 기능")
class LineIntegrationTest extends IntegrationTest {

    private LineRequest lineRequest1;
    private LineRequest lineRequest2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        final Long firstStationId = CreateHelper.createStation("잠실역");
        final Long secondStationId = CreateHelper.createStation("역삼역");
        final Long thirdStationId = CreateHelper.createStation("교대역");
        final Long fourthStationId = CreateHelper.createStation("강변역");

        lineRequest1 = new LineRequest("신분당선", "bg-red-600", firstStationId, secondStationId, 10);
        lineRequest2 = new LineRequest("구신분당선", "bg-red-600", thirdStationId, fourthStationId, 11);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssuredHelper.post("/lines", lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        RestAssuredHelper.post("/lines", lineRequest1);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.post("/lines", lineRequest1);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("빈 문자열로 지하철 노선을 생성한다.")
    @Test
    void createLineWithEmptyName() {
        // given
        final LineRequest emptyLineRequest = new LineRequest("", "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.post("/lines", emptyLineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("최대 길이를 초과한 문자열로 지하철 노선을 생성한다.")
    @Test
    void createLineWithExceedName() {
        // given
        final LineRequest exceedLineRequest = new LineRequest("가".repeat(256), "bg-red-600", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.post("/lines", exceedLineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("빈 색상명으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithEmptyColor() {
        // given
        final LineRequest emptyLineRequest = new LineRequest("지하철역", "", 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.post("/lines", emptyLineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("최대 길이를 초과한 색상명으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithExceedColor() {
        // given
        final LineRequest exceedLineRequest = new LineRequest("지하철역", "a".repeat(21), 1L, 2L, 10);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.post("/lines", exceedLineRequest);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = RestAssuredHelper.post("/lines", lineRequest1);
        ExtractableResponse<Response> createResponse2 = RestAssuredHelper.post("/lines", lineRequest2);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.get("/lines");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssuredHelper.post("/lines", lineRequest1);

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssuredHelper.get("/lines/" + lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.as(LineStationsResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(lineId);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회할 경우 예외를 던진다.")
    @Test
    void getNotExistsLine() {
        // given
        final Long notExistsLineId = 9999L;

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.get("/lines/" + notExistsLineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssuredHelper.post("/lines", lineRequest1);

        // when
        long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssuredHelper.put("/lines/" + lineId, lineRequest2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssuredHelper.post("/lines", lineRequest1);

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssuredHelper.delete("/lines/" + lineId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
