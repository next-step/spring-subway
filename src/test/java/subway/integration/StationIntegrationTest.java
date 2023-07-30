package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.domain.Station;
import subway.dto.response.StationResponse;
import subway.error.ErrorResponse;
import subway.integration.helper.StationIntegrationHelper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationIntegrationTest extends IntegrationTest {

    @DisplayName("[POST] [/stations] 지하철역 이름을 입력으로 지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = Map.of("name", "강남역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("[POST] [/stations] 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면 BAD_REQUEST 웅답을 받는다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationIntegrationHelper.createStation("강남역");
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(Map.of("name", "강남역"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("[GET] [/stations] 지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        final Station stationA = StationIntegrationHelper.createStation("강남역");
        final Station stationB = StationIntegrationHelper.createStation("역삼역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class)
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultStationIds).contains(stationA.getId(), stationB.getId());
    }

    @DisplayName("[GET] [/stations/{stationId}] 지하철역 ID 로 지하철역을 조회한다.")
    @Test
    void getStation() {
        /// given
        final Station station = StationIntegrationHelper.createStation("강남역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations/{stationId}", station.getId())
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        StationResponse stationResponse = response.as(StationResponse.class);
        assertThat(stationResponse.getId()).isEqualTo(station.getId());
    }

    @DisplayName("[GET] [/stations/{stationId}] 없는 지하철역 ID 로 지하철역을 조회하면 BAD_REQUEST 웅답을 받는다.")
    @Test
    void notFoundStation() {
        // given , when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/stations/{stationId}", 1L)
                .then().log().all()
                .extract();

        // then
        final ErrorResponse errorResponse = response.body().as(ErrorResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.getMessage()).isEqualTo("역을 찾을 수 없습니다.");
    }

    @DisplayName("[PUT] [/stations/{stationId}] 지하철역 ID , 지하철역 이름으로 지하철역을 수정한다.")
    @Test
    void updateStation() {
        // given
        Station station = StationIntegrationHelper.createStation("강남역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(Map.of("name", "삼성역"))
                .when()
                .put("/stations/{stationId}", station.getId())
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("[DELETE] [/stations/{stationId}] 노선 ID로 지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Station station = StationIntegrationHelper.createStation("강남역");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/stations/{stationId}", station.getId())
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
