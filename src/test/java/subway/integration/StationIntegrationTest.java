package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.HttpStatusAssertions.assertIsBadRequest;
import static subway.integration.HttpStatusAssertions.assertIsCreated;
import static subway.integration.HttpStatusAssertions.assertIsNoContent;
import static subway.integration.HttpStatusAssertions.assertIsOk;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.StationCreateRequest;
import subway.dto.StationResponse;
import subway.dto.StationUpdateRequest;

@DisplayName("지하철역 관련 기능")
class StationIntegrationTest extends IntegrationTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        String stationName = "강남역";

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.createStation(
                new StationCreateRequest(stationName));

        // then
        assertIsCreated(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        String stationName = "강남역";
        StationIntegrationSupporter.createStation(new StationCreateRequest(stationName));

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.createStation(
                new StationCreateRequest(stationName));

        // then
        assertIsBadRequest(response);
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        String stationUri1 = createStationAndGetLocation("강남역");

        String stationUri2 = createStationAndGetLocation("역삼역");

        List<Long> expectedStationIds = Stream.of(stationUri1, stationUri2)
                .map(station -> Long.valueOf(station.split("/")[2]))
                .collect(Collectors.toList());

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.findAllStation();

        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        // then
        assertIsOk(response);
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStation() {
        /// given
        long stationId = Long.parseLong(createStationAndGetLocation("강남역").split("/")[2]);

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.getStationByStationId(stationId);

        // then
        assertIsOk(response);
        assertThat(response.as(StationResponse.class).getId()).isEqualTo(stationId);
    }

    @DisplayName("지하철역을 수정한다.")
    @Test
    void updateStation() {
        // given
        String uri = createStationAndGetLocation("강남역");

        String updateName = "삼성역";
        StationUpdateRequest stationUpdateRequest = new StationUpdateRequest(updateName);

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.updateStation(uri, stationUpdateRequest);

        // then
        assertIsOk(response);
    }

    @Test
    @DisplayName("지하철역을 제거한다.")
    void deleteStation() {
        // given
        String uri = createStationAndGetLocation("강남역");

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.deleteStation(uri);

        // then
        assertIsNoContent(response);
    }

    private String createStationAndGetLocation(String stationName) {
        ExtractableResponse<Response> createResponse = StationIntegrationSupporter.createStation(
                new StationCreateRequest(stationName));
        return createResponse.header("Location");
    }
}
