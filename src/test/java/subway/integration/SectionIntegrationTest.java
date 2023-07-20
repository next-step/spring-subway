package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.SectionRequest;
import subway.helper.CreateHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @DisplayName("하행 종점역을 연장하는 지하철 구간을 생성한다.")
    @Test
    void createDownwardSection() {
        // given
        CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123");

        List<Long> stationIds = CreateHelper.getStationIds();

        SectionRequest params = new SectionRequest(stationIds.get(1), stationIds.get(2), 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("상행 종점역을 연장하는 지하철 구간을 생성한다.")
    @Test
    void createUpwardSection() {
        // given
        CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123");

        List<Long> stationIds = CreateHelper.getStationIds();

        SectionRequest params = new SectionRequest(stationIds.get(2), stationIds.get(0), 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("상행역을 기준으로 중간에 역을 삽입하여 지하철 구간을 생성한다.")
    @Test
    void createUpInsertSection() {
        // given
        CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123");

        List<Long> stationIds = CreateHelper.getStationIds();

        SectionRequest params = new SectionRequest(stationIds.get(0), stationIds.get(2), 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("하행역을 기준으로 중간에 역을 삽입하여 지하철 구간을 생성한다.")
    @Test
    void createDownInsertSection() {
        // given
        CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123");

        List<Long> stationIds = CreateHelper.getStationIds();

        SectionRequest params = new SectionRequest(stationIds.get(2), stationIds.get(1), 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 역으로 지하철 구간을 생성한다.")
    @Test
    void createAlreadyExist() {
        // given
        Long lineId = CreateHelper.createLine("2호선", "bg-123");

        List<Long> stationIds = CreateHelper.getStationIds();

        SectionRequest params = new SectionRequest(stationIds.get(0), stationIds.get(1), 10);
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        SectionRequest params2 = new SectionRequest(stationIds.get(1), stationIds.get(0), 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params2)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("기존에 존재하지 않는 지하철 역으로 지하철 구간을 생성한다.")
    @Test
    void createNotExist() {
        // given
        CreateHelper.createStation("교대역");
        CreateHelper.createStation("선릉역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123");

        List<Long> stationIds = CreateHelper.getStationIds();

        SectionRequest params = new SectionRequest(stationIds.get(2), stationIds.get(3), 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("같은 역으로 지하철 구간을 생성한다.")
    @Test
    void createSame() {
        // given
        Long lineId = CreateHelper.createLine("2호선", "bg-123");

        List<Long> stationIds = CreateHelper.getStationIds();

        SectionRequest params = new SectionRequest(stationIds.get(0), stationIds.get(0), 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }


    @DisplayName("중간에 기존 구간보다 길이가 긴 지하철 구간을 생성한다.")
    @Test
    void createTooLongSection() {
        // given
        CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123");

        List<Long> stationIds = CreateHelper.getStationIds();

        SectionRequest params = new SectionRequest(stationIds.get(0), stationIds.get(2), 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        CreateHelper.createStation("잠실역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123");

        List<Long> stationIds = CreateHelper.getStationIds();
        CreateHelper.createSection(stationIds, 0, 1, lineId);
        CreateHelper.createSection(stationIds, 1, 2, lineId);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationIds.get(2))
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("마지막 남은 지하철 구간을 제거한다.")
    @Test
    void deletelastSection() {
        // given
        Long lineId = CreateHelper.createLine("2호선", "bg-123");

        List<Long> stationIds = CreateHelper.getStationIds();
        CreateHelper.createSection(stationIds, 0, 1, lineId);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationIds.get(1))
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
