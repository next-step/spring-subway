package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.SectionRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    private SectionRequest sectionRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();

        sectionRequest = new SectionRequest(12L, 13L, 777L);
    }

    @Test
    @DisplayName("지하철 구간을 생성한다.")
    void createSection() {
        /* given */
        final Long lineId = 1L;

        /* when */
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @Test
    @DisplayName("새로운 구간의 상행역이 해당 노선에 등록되어 있는 하행 종점역이 아닌 경우 400 Bad Request로 응답한다.")
    void badRequestWithNotRegisteredLastDownStation() {
        /* given */
        final SectionRequest sectionRequest = new SectionRequest(11L, 14L, 777L);
        final Long lineId = 1L;

        /* when */
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("새로운 구간의 상행역이 해당 노선에 등록되어 있는 하행 종점역이 아닙니다.");
    }

    @Test
    @DisplayName("상행 역과 하행 역이 이미 노선에 모두 등록되어 있는 경우 400 Bad Request로 응답한다.")
    void badRequestWithRegisteredUpStationAndDownStationOnLine() {
        /* given */
        final SectionRequest sectionRequest = new SectionRequest(23L, 24L, 777L);
        final Long lineId = 2L;

        /* when */
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("상행 역과 하행 역이 이미 노선에 모두 등록되어 있습니다.");
    }

    @Test
    @DisplayName("상행 역과 하행 역이 모두 노선에 없는 경우 400 Bad Request로 응답한다.")
    void badRequestWithNotExistUpStationAndDownStationOnLine() {
        /* given */
        final SectionRequest sectionRequest = new SectionRequest(12345L, 1234L, 777L);
        final Long lineId = 2L;

        /* when */
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("상행 역과 하행 역이 모두 노선에 없습니다.");
    }

    @Test
    @DisplayName("지하철 구간을 제거한다.")
    void deleteSection() {
        /* given */
        final Long lineId = 2L;

        /* when */
        ExtractableResponse<Response> response = RestAssured
                .given().log().all().queryParam("stationId", "25")
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("지하철 노선에 등록된 하행 종점역이 아닌 경우 삭제 시 400 Bad Request로 응답한다.")
    void badRequestWithNotPrevSectionOnLine() {
        /* given */
        final Long lineId = 2L;

        /* when */
        ExtractableResponse<Response> response = RestAssured
                .given().log().all().queryParam("stationId", "24")
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("해당 노선에 일치하는 하행 종점역이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("지하철 노선에 구간이 하나인 경우 삭제 시 400 Bad Request로 응답한다.")
    void badRequestWithOnlyOneSection() {
        /* given */
        final Long lineId = 1L;

        /* when */
        ExtractableResponse<Response> response = RestAssured
                .given().log().all().queryParam("stationId", "12")
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("해당 노선에 구간이 하나여서 제거할 수 없습니다.");
    }
}
