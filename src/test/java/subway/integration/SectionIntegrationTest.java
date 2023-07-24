package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.SectionRequest;
import subway.helper.CreateHelper;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
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
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 구간을 생성할 때 중복된 역이 있으면 예외를 던진다.")
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

    @DisplayName("지하철 구간을 생성할 때 상행역과 하행역이 같으면 예외를 던진다.")
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

    @DisplayName("하행 종점역과 새로운 구간의 상행역이 다르면 예외를 던진다.")
    @Test
    void createNotSame() {
        // given
        CreateHelper.createStation("강남역");
        CreateHelper.createStation("역삼역");
        CreateHelper.createStation("잠실역");
        CreateHelper.createStation("강변역");
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
}
