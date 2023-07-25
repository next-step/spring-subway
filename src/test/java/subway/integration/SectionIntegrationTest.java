package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineWithStationsResponse;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

@DisplayName("지하철 구간 관련 기능 인수 테스트")
class SectionIntegrationTest extends IntegrationTest {

    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;
    private Long stationId5;
    private Long stationId6;
    private Long lineId;
    private SectionRequest sectionRequest1;
    private SectionRequest sectionRequest2;

    @BeforeEach
    public void setUp() {
        super.setUp();
        setUpStationsAndLine();
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("노선이 존재하지 않는데 지하철 구간을 생성할 시 예외 발생")
    @Test
    void createSectionLineNotFound() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/{lineId}/sections", 99999)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("상행역이 존재하지 않는데 지하철 구간을 생성할 시 예외 발생")
    @Test
    void createSectionUpStationNotFound() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(99999L, stationId1, 10L))
                .when().post("/lines/{lineId}/sections", 1L)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("하행역이 존재하지 않는데 지하철 구간을 생성할 시 예외 발생")
    @Test
    void createSectionDownStationNotFound() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(stationId2, 99999L, 10L))
                .when().post("/lines/{lineId}/sections", 1L)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("입력값 잘못된 상태로 지하철 구간 생성시 예외 발생")
    @Test
    void createSectionValidationFail() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(stationId2, -1L, 10L))
                .when().post("/lines/{lineId}/sections", 1L)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선의 하행 종점역이 있는 구간을 제거한다.")
    @Test
    void deleteLastStationOfLine() {
        // given
        ExtractableResponse<Response> createResponse1 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();
        ExtractableResponse<Response> createResposne2 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest2)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // when

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("stationId", sectionRequest2.getDownStationId())
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선의 상행 종점역이 있는 구간을 제거한다.")
    @Test
    void deleteFirstStationOfLine() {
        // given
        ExtractableResponse<Response> createResponse1 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();
        ExtractableResponse<Response> createResposne2 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest2)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // when

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("stationId", sectionRequest1.getUpStationId())
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선의 중간역이 있는 구간을 제거한다.")
    @Test
    void deleteMiddleStationOfLine() {
        // given
        ExtractableResponse<Response> createResponse1 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();
        ExtractableResponse<Response> createResposne2 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest2)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("stationId", sectionRequest1.getDownStationId())
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        List<StationResponse> stations = RestAssured
                .given().log().all()
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract()
                .as(LineWithStationsResponse.class)
                .getStations();
        assertThat(stations).hasSize(3);
    }

    @DisplayName("지하철 노선의 구간이 하나면 제거에 실패한다.")
    @Test
    void deleteStationOfLineWithOneSectionThenFail() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("stationId", stationId1)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선의 없는 역은 제거에 실패한다.")
    @Test
    void deleteNotStationOfLineThenFail() {
        // given
        ExtractableResponse<Response> createResponse1 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("stationId", 99999L)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void setUpStationsAndLine() {
        stationId1 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new StationRequest("강남"))
                .when().post("/stations")
                .then().log().all()
                .extract()
                .as(StationResponse.class)
                .getId();
        stationId2 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new StationRequest("역삼"))
                .when().post("/stations")
                .then().log().all()
                .extract()
                .as(StationResponse.class)
                .getId();
        stationId3 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new StationRequest("선릉"))
                .when().post("/stations")
                .then().log().all()
                .extract()
                .as(StationResponse.class)
                .getId();
        stationId4 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new StationRequest("삼성"))
                .when().post("/stations")
                .then().log().all()
                .extract()
                .as(StationResponse.class)
                .getId();
        stationId5 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new StationRequest("종합운동장"))
                .when().post("/stations")
                .then().log().all()
                .extract()
                .as(StationResponse.class)
                .getId();
        stationId6 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new StationRequest("잠실새내"))
                .when().post("/stations")
                .then().log().all()
                .extract()
                .as(StationResponse.class)
                .getId();

        lineId = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LineRequest("3호선", "#555555", stationId1, stationId2, 10L))
                .when().post("/lines")
                .then().log().all()
                .extract()
                .as(LineResponse.class)
                .getId();

        sectionRequest1 = new SectionRequest(stationId2, stationId3, 10L);
        sectionRequest2 = new SectionRequest(stationId3, stationId4, 10L);
    }
}
