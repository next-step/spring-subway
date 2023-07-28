package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.request.CreateStationRequest;
import subway.dto.request.UpdateStationRequest;
import subway.dto.response.StationResponse;

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
        String stationName = "강남역";

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.createStation(new CreateStationRequest(stationName));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String stationName = "강남역";
        StationIntegrationSupporter.createStation(new CreateStationRequest(stationName));

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.createStation(new CreateStationRequest(stationName));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        String stationName1 = "강남역";
        ExtractableResponse<Response> createResponse1 = StationIntegrationSupporter.createStation(new CreateStationRequest(stationName1));

        String stationName2 = "역삼역";
        ExtractableResponse<Response> createResponse2 = StationIntegrationSupporter.createStation(new CreateStationRequest(stationName2));

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.findAllStation();

        List<Long> expectedStationIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.valueOf(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStation() {
        /// given
        String stationName = "강남역";
        ExtractableResponse<Response> createResponse = StationIntegrationSupporter.createStation(new CreateStationRequest(stationName));

        Long stationId = Long.valueOf(createResponse.header("Location").split("/")[2]);

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.getStationByStationId(stationId);
        StationResponse stationResponse = response.as(StationResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(stationResponse.getId()).isEqualTo(stationId);
    }

    @DisplayName("지하철역을 수정한다.")
    @Test
    void updateStation() {
        // given
        String stationName = "강남역";
        ExtractableResponse<Response> createResponse = StationIntegrationSupporter.createStation(new CreateStationRequest(stationName));

        String updateName = "삼성역";
        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.updateStation(uri,
                new UpdateStationRequest(updateName));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        String stationName = "강남역";
        ExtractableResponse<Response> createResponse = StationIntegrationSupporter.createStation(new CreateStationRequest(stationName));

        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.deleteStation(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
