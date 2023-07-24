package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
class StationIntegrationTest extends IntegrationTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        final StationRequest request = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.createStation(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        final StationRequest request = new StationRequest("강남역");
        StationIntegrationSupporter.createStation(request);

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.createStation(request);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        final StationRequest request1 = new StationRequest("강남역");
        final StationRequest request2 = new StationRequest("역삼역");

        ExtractableResponse<Response> createResponse1 = StationIntegrationSupporter.createStation(request1);
        ExtractableResponse<Response> createResponse2 = StationIntegrationSupporter.createStation(request2);

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.findAllStations();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedStationIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStation() {
        /// given
        final StationRequest request = new StationRequest("강남역");

        ExtractableResponse<Response> createResponse = StationIntegrationSupporter.createStation(request);

        // when
        final Long stationId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = StationIntegrationSupporter.findStation(stationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        StationResponse stationResponse = response.as(StationResponse.class);
        assertThat(stationResponse.getId()).isEqualTo(stationId);
    }

    @DisplayName("지하철역을 수정한다.")
    @Test
    void updateStation() {
        // given
        final StationRequest request1 = new StationRequest("강남역");

        ExtractableResponse<Response> createResponse = StationIntegrationSupporter.createStation(request1);

        // when
        final String uri = createResponse.header("Location");

        final StationRequest request2 = new StationRequest("삼성역");
        ExtractableResponse<Response> response = StationIntegrationSupporter.updateStation(uri, request2);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        final StationRequest request = new StationRequest("강남역");

        ExtractableResponse<Response> createResponse = StationIntegrationSupporter.createStation(request);

        // when
        final String uri = createResponse.header("Location");

        ExtractableResponse<Response> response = StationIntegrationSupporter.deleteStation(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
