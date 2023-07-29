package subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import subway.acceptance.helper.LineHelper;
import subway.acceptance.helper.RestHelper;
import subway.acceptance.helper.SectionHelper;
import subway.acceptance.helper.StationHelper;
import subway.dto.request.SectionCreateRequest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능 인수 테스트")
class SectionAcceptanceTest extends AcceptanceTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        setUpStations();
        setUpLines();
    }

    @Test
    @DisplayName("새로운 역을 상행 종점역으로 등록할 수 있다.")
    void createFirstSection() {
        /* given */
        final Long lineId = 1L;
        final SectionCreateRequest request = new SectionCreateRequest(3L, 1L, 50);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.post(request, "/lines/{lineId}/sections", List.of(lineId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/lines/" + lineId);
    }

    @Test
    @DisplayName("새로운 역을 하행 종점역으로 등록할 수 있다.")
    void createLastSection() {
        /* given */
        final Long lineId = 1L;
        final SectionCreateRequest request = new SectionCreateRequest(2L, 3L, 50);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.post(request, "/lines/{lineId}/sections", List.of(lineId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/lines/" + lineId);
    }


    @Test
    @DisplayName("요청의 상행역과 노선에 속한 구간의 상행역이 같은 구간을 등록할 수 있다.")
    void createSectionWhenRequestUpStationExists() {
        /* given */
        final Long lineId = 1L;
        final SectionCreateRequest request = new SectionCreateRequest(1L, 3L, 50);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.post(request, "/lines/{lineId}/sections", List.of(lineId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/lines/" + lineId);
    }

    @Test
    @DisplayName("요청의 하행역과 노선에 속한 구간의 하행역이 같은 구간을 등록할 수 있다.")
    void createSectionWhenRequestDownStationExists() {
        /* given */
        final Long lineId = 1L;
        final SectionCreateRequest request = new SectionCreateRequest(3L, 2L, 50);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.post(request, "/lines/{lineId}/sections", List.of(lineId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/lines/" + lineId);
    }

    @ParameterizedTest
    @ValueSource(ints = {777, 1_234})
    @DisplayName("새로운 구간의 길이가 기존 구간의 길이보다 크거나 같으면 400 Bad Request로 응답한다.")
    void badRequestWithGreaterThanOrEqualExistSectionDistance(final int distance) {
        /* given */
        final Long lineId = 1L;
        final SectionCreateRequest request = new SectionCreateRequest(3L, 2L, distance);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.post(request, "/lines/{lineId}/sections", List.of(lineId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("새로운 구간의 길이는 기존 구간의 길이보다 짧아야 합니다.");
    }

    @Test
    @DisplayName("상행 역과 하행 역이 이미 노선에 모두 등록되어 있는 경우 400 Bad Request로 응답한다.")
    void badRequestWithRegisteredUpStationAndDownStationOnLine() {
        /* given */
        final Long lineId = 1L;
        final SectionCreateRequest request = new SectionCreateRequest(1L, 2L, 50);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.post(request, "/lines/{lineId}/sections", List.of(lineId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("상행 역과 하행 역이 이미 노선에 모두 등록되어 있습니다.");
    }

    @Test
    @DisplayName("상행 역과 하행 역이 모두 노선에 없는 경우 400 Bad Request로 응답한다.")
    void badRequestWithNotExistUpStationAndDownStationOnLine() {
        /* given */
        final Long lineId = 1L;
        final SectionCreateRequest request = new SectionCreateRequest(1_234L, 5_678L, 50);

        /* when */
        final ExtractableResponse<Response> response = RestHelper.post(request, "/lines/{lineId}/sections", List.of(lineId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("상행 역과 하행 역이 모두 노선에 없습니다.");
    }

    @Test
    @DisplayName("지하철 노선의 상행 종점역을 포함하는 구간을 제거한다.")
    void deleteSectionContainsFirstSection() {
        /* given */
        final Long lineId = 1L;
        final Long targetStationId = 3L;
        SectionHelper.createSection(lineId, targetStationId, 1L, 50);

        /* when */
        final ExtractableResponse<Response> response =
                RestHelper.delete("/lines/{lineId}/sections", List.of(lineId), Map.of("stationId", targetStationId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("지하철 노선의 하행 종점역을 포함하는 구간을 제거한다.")
    void deleteSectionContainsLastStation() {
        /* given */
        final Long lineId = 1L;
        final Long targetStationId = 3L;
        SectionHelper.createSection(lineId, 2L, targetStationId, 50);

        /* when */
        final ExtractableResponse<Response> response =
                RestHelper.delete("/lines/{lineId}/sections", List.of(lineId), Map.of("stationId", targetStationId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("지하철 노선의 중간역을 포함하는 구간을 제거한다.")
    void deleteSection() {
        /* given */
        final Long lineId = 1L;
        final Long targetStationId = 3L;
        SectionHelper.createSection(lineId, 1L, targetStationId, 50);

        /* when */
        final ExtractableResponse<Response> response =
                RestHelper.delete("/lines/{lineId}/sections", List.of(lineId), Map.of("stationId", targetStationId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("지하철 노선에 구간이 하나인 경우 삭제 시 400 Bad Request로 응답한다.")
    void badRequestWithOnlyOneSection() {
        /* given */
        final Long lineId = 1L;
        final Long targetStationId = 1L;

        /* when */
        final ExtractableResponse<Response> response =
                RestHelper.delete("/lines/{lineId}/sections", List.of(lineId), Map.of("stationId", targetStationId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("해당 노선에 구간이 하나여서 제거할 수 없습니다.");
    }

    private void setUpStations() {
        StationHelper.createStation("잠실");
        StationHelper.createStation("구로디지털단지");
        StationHelper.createStation("신대방");
    }

    private void setUpLines() {
        LineHelper.createLine("2호선", "초록색", 1L, 2L, 777);
    }
}
