package subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.acceptance.helper.LineHelper;
import subway.acceptance.helper.RestHelper;
import subway.acceptance.helper.StationHelper;
import subway.dto.request.LineCreateRequest;
import subway.dto.request.LineUpdateRequest;
import subway.dto.response.LineFindResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능 인수 테스트")
class LineAcceptanceTest extends AcceptanceTest {

    @BeforeEach
    public void setUp() {
        super.setUp();

        setUpStations();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        /* given */
        final LineCreateRequest request =
                new LineCreateRequest("2호선", "초록색", 1L, 2L, 777);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.post(request, "/lines");

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/lines/1");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        /* given */
        LineHelper.createLine("2호선", "초록색", 1L, 2L, 777);

        /* given */
        final ExtractableResponse<Response> response =
                LineHelper.createLine("2호선", "초록색", 1L, 2L, 777);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        /* given */
        final ExtractableResponse<Response> createResponse1 = LineHelper.createLine("2호선", "초록색", 1L, 2L, 777);
        final ExtractableResponse<Response> createResponse2 = LineHelper.createLine("3호선", "주황색", 3L, 4L, 777);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.get("/lines");

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        final List<Long> resultLineIds = response.jsonPath().getList(".", LineFindResponse.class).stream()
                .map(LineFindResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        /* given */
        final ExtractableResponse<Response> createResponse = LineHelper.createLine("2호선", "초록색", 1L, 2L, 777);
        final Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.get("/lines/{lineId}", List.of(lineId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final LineFindResponse resultResponse = response.as(LineFindResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(lineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        /* given */
        final ExtractableResponse<Response> createResponse = LineHelper.createLine("2호선", "초록색", 1L, 2L, 777);
        final Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        final LineUpdateRequest request = new LineUpdateRequest("3호선", "주황색", 3L, 4L, 777);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.put(request, "/lines/{lineId}", List.of(lineId));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        /* given */
        final ExtractableResponse<Response> createResponse = LineHelper.createLine("2호선", "초록색", 1L, 2L, 777);
        final Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.delete("/lines/{lineId}", List.of(lineId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void setUpStations() {
        StationHelper.createStation("잠실");
        StationHelper.createStation("구로디지털단지");
        StationHelper.createStation("신대방");
        StationHelper.createStation("서울대입구");
    }
}
