package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.TestSettingUtils.createStationWith;
import static subway.integration.TestSettingUtils.createStationsWithNames;
import static subway.integration.TestSettingUtils.extractCreatedId;
import static subway.integration.TestSettingUtils.extractCreatedIds;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

@DisplayName("지하철역 관련 기능")
class StationIntegrationTest extends IntegrationTest {

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        //given
        final StationRequest stationRequest = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        final StationRequest stationRequest = new StationRequest("강남역");
        createStationWith(stationRequest);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        final List<ExtractableResponse<Response>> responses = createStationsWithNames("사당역",
            "방배역", "서초역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/stations")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final List<Long> expectedStationIds = extractCreatedIds(responses);
        final List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class)
            .stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStation() {
        /// given
        final StationRequest stationRequest = new StationRequest("사당역");
        final Long stationId = extractCreatedId(createStationWith(stationRequest));

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
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
        final StationRequest stationRequest = new StationRequest("사당역");
        final ExtractableResponse<Response> createResponse = createStationWith(stationRequest);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new StationRequest("삼성역"))
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        final StationRequest stationRequest = new StationRequest("사당역");
        final ExtractableResponse<Response> createResponse = createStationWith(stationRequest);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
