package subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.acceptance.helper.RestHelper;
import subway.acceptance.helper.StationHelper;
import subway.dto.request.StationCreateRequest;
import subway.dto.response.StationFindResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.acceptance.helper.RestHelper.post;

@DisplayName("지하철역 관련 기능 인수 테스트")
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        /* given */
        final StationCreateRequest request = new StationCreateRequest("잠실");

        /* when */
        final ExtractableResponse<Response> response = post(request, "/stations");

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isEqualTo("/stations/1");
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        /* given */
        StationHelper.createStation("잠실");
        final StationCreateRequest request = new StationCreateRequest("잠실");

        /* when */
        final ExtractableResponse<Response> response = post(request, "/stations");

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /* given */
        final ExtractableResponse<Response> createResponse1 = StationHelper.createStation("잠실");
        final ExtractableResponse<Response> createResponse2 = StationHelper.createStation("역삼역");

        /* when */
        final ExtractableResponse<Response> response = RestHelper.get("/stations");

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<Long> expectedStationIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        final List<Long> resultStationIds = response.jsonPath().getList(".", StationFindResponse.class).stream()
                .map(StationFindResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStation() {
        /* given */
        final ExtractableResponse<Response> createResponse = StationHelper.createStation("잠실");

        /* when */
        final Long stationId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        final ExtractableResponse<Response> response = RestHelper.get("/stations/{stationId}", List.of(stationId));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final StationFindResponse stationFindResponse = response.as(StationFindResponse.class);
        assertThat(stationFindResponse.getId()).isEqualTo(stationId);
    }

    @DisplayName("지하철역을 수정한다.")
    @Test
    void updateStation() {
        /* given */
        final ExtractableResponse<Response> createResponse = StationHelper.createStation("잠실");
        final StationCreateRequest request = new StationCreateRequest("구로디지털단지");

        /* when */
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = RestHelper.put(request, uri);

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        /* given */
        final ExtractableResponse<Response> createResponse = StationHelper.createStation("잠실");

        /* when */
        final String uri = createResponse.header("Location");
        final ExtractableResponse<Response> response = RestHelper.delete(uri);

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
