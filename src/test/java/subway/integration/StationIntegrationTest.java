package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.ExceptionResponse;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.util.TestRequestUtil.createStation;
import static subway.util.TestRequestUtil.extractId;

@DisplayName("지하철역 관련 기능 통합 테스트")
public class StationIntegrationTest extends IntegrationTest {
    @Test
    @DisplayName("지하철역을 생성한다.")
    void createStationTest() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
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

    @Test
    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성할 수 없다.")
    void createStationWithDuplicateName() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        createStation(stationRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then()
                .log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.STATION_NAME_DUPLICATE.getMessage() + "강남역");
    }

    @Test
    @DisplayName("지하철역 목록을 조회한다.")
    void getStations() {
        /// given
        StationRequest stationRequest1 = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse1 = createStation(stationRequest1);

        StationRequest stationRequest2 = new StationRequest("역삼역");
        ExtractableResponse<Response> createResponse2 = createStation(stationRequest2);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedStationIds = Stream.of(createResponse1, createResponse2)
                .map(it -> extractId(it))
                .collect(Collectors.toList());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds).containsAll(expectedStationIds);
    }


    @Test
    @DisplayName("지하철역을 조회한다.")
    void getStation() {
        /// given
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = createStation(stationRequest);

        // when
        Long stationId = extractId(createResponse);
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

    @Test
    @DisplayName("존재하지 않는 id로 지하철역을 조회한다.")
    void getStationNonExist() {
        /// given
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = createStation(stationRequest);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations/{stationId}", 5L)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.STATION_ID_NO_EXIST.getMessage() + "5");
    }

    @Test
    @DisplayName("지하철역을 수정한다.")
    void updateStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = createStation(stationRequest);

        // when
        StationRequest updateRequest = new StationRequest("역삼역");
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updateRequest)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("기존에 존재하는 이름으로 지하철역을 수정할 수 없다.")
    void updateStationDuplicateName() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = createStation(stationRequest);

        StationRequest station2Request = new StationRequest("역삼역");
        createStation(station2Request);

        // when
        StationRequest updateRequest = new StationRequest("역삼역");
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(updateRequest)
                .when()
                .put(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.STATION_NAME_DUPLICATE.getMessage() + "역삼역");
    }

    @Test
    @DisplayName("지하철역을 제거한다.")
    void deleteStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = createStation(stationRequest);

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
