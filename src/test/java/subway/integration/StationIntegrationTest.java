package subway.integration;

import static subway.integration.HttpStatusAssertions.assertIsNoContent;
import static subway.integration.HttpStatusAssertions.assertIsOk;
import static subway.integration.StationIntegrationAssertions.assertIsDuplicatedStationName;
import static subway.integration.StationIntegrationAssertions.assertIsStationCreated;
import static subway.integration.StationIntegrationAssertions.assertIsStationFound;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.StationCreateRequest;
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
        assertIsStationCreated(response);
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
        assertIsDuplicatedStationName(response);
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationIntegrationSupporter.createStation(new StationCreateRequest("강남역"));
        StationIntegrationSupporter.createStation(new StationCreateRequest("역삼역"));

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.findAllStation();

        // then
        assertIsStationFound(response, 2);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStation() {
        /// given
        long stationId = Long.parseLong(createStationAndGetLocation("강남역").split("/")[2]);

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.getStationByStationId(stationId);

        // then
        assertIsStationFound(response);
    }

    @DisplayName("지하철역을 수정한다.")
    @Test
    void updateStation() {
        // given
        String uri = createStationAndGetLocation("부천역");

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
