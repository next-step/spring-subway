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
import subway.RestApiUtils;
import subway.ui.dto.StationRequest;
import subway.ui.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
class StationIntegrationTest extends IntegrationTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest request = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = RestApiUtils.post(request, "/stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationRequest request = new StationRequest("강남역");
        RestApiUtils.post(request, "/stations");

        // when
        ExtractableResponse<Response> response = RestApiUtils.post(request, "stations");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest request1 = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse1 = RestApiUtils.post(request1, "/stations");

        StationRequest request2 = new StationRequest("역삼역");
        ExtractableResponse<Response> createResponse2 = RestApiUtils.post(request2, "/stations");

        // when
        ExtractableResponse<Response> response = RestApiUtils.get("/stations");

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
        StationRequest request1 = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = RestApiUtils.post(request1, "/stations");

        // when
        Long stationId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/stations/{stationId}", stationId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        StationResponse stationResponse = response.as(StationResponse.class);
        assertThat(stationResponse.getId()).isEqualTo(stationId);
    }

    @DisplayName("지하철역을 수정한다.")
    @Test
    void updateStation() {
        // given
        StationRequest request1 = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = RestApiUtils.post(request1, "/stations");

        // when
        String uri = createResponse.header("Location");
        StationRequest request2 = new StationRequest("삼성역");
        ExtractableResponse<Response> response = RestApiUtils.put(request2, uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest request1 = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = RestApiUtils.post(request1, "/stations");

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestApiUtils.delete(uri);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
