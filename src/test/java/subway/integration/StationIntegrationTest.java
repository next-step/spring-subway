package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.request.StationRequest;
import subway.dto.response.StationResponse;
import subway.integration.fixture.SubWayFixture;

@DisplayName("지하철역 관련 기능")
class StationIntegrationTest extends IntegrationTest {

    static void createInitialStations() {
        SubWayFixture.createStation(new StationRequest("A"));
        SubWayFixture.createStation(new StationRequest("B"));
        SubWayFixture.createStation(new StationRequest("C"));
        SubWayFixture.createStation(new StationRequest("D"));
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = SubWayFixture.createStation(
            new StationRequest("강남역"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        SubWayFixture.createStation(new StationRequest("강남역"));

        // when
        ExtractableResponse<Response> response = SubWayFixture.createStation(
            new StationRequest("강남역"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = SubWayFixture.createStation(
            new StationRequest("강남역"));

        ExtractableResponse<Response> createResponse2 = SubWayFixture.createStation(
            new StationRequest("역삼역"));

        // when
        ExtractableResponse<Response> response = SubWayFixture.findAllStations();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedStationIds = Stream.of(createResponse1, createResponse2)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class)
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStation() {
        /// given
        ExtractableResponse<Response> createResponse = SubWayFixture.createStation(
            new StationRequest("강남역"));

        // when
        Long stationId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = SubWayFixture.findStationById(stationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        StationResponse stationResponse = response.as(StationResponse.class);
        assertThat(stationResponse.getId()).isEqualTo(stationId);
    }

    @DisplayName("지하철역을 수정한다.")
    @Test
    void updateStation() {
        // given
        ExtractableResponse<Response> createResponse = SubWayFixture.createStation(
            new StationRequest("강남역"));

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = updateStation(uri, new StationRequest("삼성역"));

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private static ExtractableResponse<Response> updateStation(String uri,
        StationRequest stationRequest) {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(stationRequest)
            .when()
            .put(uri)
            .then().log().all()
            .extract();
        return response;
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = SubWayFixture.createStation(
            new StationRequest("강남역"));

        // when
        long stationId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = SubWayFixture.deleteStationById(stationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
