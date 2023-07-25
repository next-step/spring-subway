package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.SectionRequest;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("요청의 상행역과 노선에 속한 구간의 상행역이 같은 구간을 등록할 수 있다.")
    void createSectionWhenRequestUpStationExists() {
        /* given */
        final Long lineId = 2L;
        final SectionRequest sectionRequest = new SectionRequest(24L, 26L, 66L);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
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
    @DisplayName("요청의 하행역과 노선에 속한 구간의 하행역이 같은 구간을 등록할 수 있다.")
    void createSectionWhenRequestDownStationExists() {
        /* given */
        final Long lineId = 2L;
        final SectionRequest sectionRequest = new SectionRequest(26L, 24L, 66L);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
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
    @DisplayName("새로운 구간의 길이가 기존 구간의 길이보다 크거나 같으면 400 Bad Request로 응답한다.")
    void badRequestWithGreaterThanOrEqualExistSectionDistance() {
        /* given */
        final Long lineId = 2L;
        final SectionRequest sectionRequest1 = new SectionRequest(26L, 24L, 888L);
        final SectionRequest sectionRequest2 = new SectionRequest(24L, 26L, 888L);

        /* when */
        final ExtractableResponse<Response> response1 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        final ExtractableResponse<Response> response2 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest2)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response1.body().jsonPath().getString("message"))
                .isEqualTo("새로운 구간의 길이는 기존 구간의 길이보다 짧아야 합니다.");
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.body().jsonPath().getString("message"))
                .isEqualTo("새로운 구간의 길이는 기존 구간의 길이보다 짧아야 합니다.");
    }

    @Test
    @DisplayName("새로운 역을 상행 종점역으로 등록할 수 있다.")
    void createFirstSection() {
        /* given */
        final Long lineId = 1L;
        final SectionRequest sectionRequest = new SectionRequest(10L, 11L, 777L);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
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
    @DisplayName("새로운 역을 하행 종점역으로 등록할 수 있다.")
    void createSection() {
        /* given */
        final Long lineId = 1L;
        final SectionRequest sectionRequest = new SectionRequest(12L, 13L, 777L);

        /* when */
        final ExtractableResponse<Response> response = RestAssured
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
    @DisplayName("상행 역과 하행 역이 이미 노선에 모두 등록되어 있는 경우 400 Bad Request로 응답한다.")
    void badRequestWithRegisteredUpStationAndDownStationOnLine() {
        /* given */
        final Long upStationId = 23L;
        final Long downStationId = 24L;
        final SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 777L);
        final Long lineId = 2L;

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("상행 역과 하행 역이 이미 노선에 모두 등록되어 있습니다. 상행 역 ID : " + upStationId + " 하행 역 ID : " + downStationId);
    }

    @Test
    @DisplayName("상행 역과 하행 역이 모두 노선에 없는 경우 400 Bad Request로 응답한다.")
    void badRequestWithNotExistUpStationAndDownStationOnLine() {
        /* given */
        final Long upStationId = 12345L;
        final Long downStationId = 1234L;
        final SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 777L);
        final Long lineId = 2L;

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("상행 역과 하행 역이 모두 노선에 없습니다. 상행 역 ID : " + upStationId + " 하행 역 ID : " + downStationId);
    }

    @Test
    @DisplayName("지하철 하행 종점역을 포함한 구간(마지막 구간)을 제거한다.")
    void deleteLastSection() {
        /* given */
        final Long lineId = 2L;
        final Long lastStationId = 25L;

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all().queryParam("stationId", lastStationId)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("지하철 상행 종점역을 포함한 구간(처음 구간)을 제거한다.")
    void deleteFirstSection() {
        /* given */
        final Long lineId = 3L;
        final Long firstStationId = 35L;

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all().queryParam("stationId", firstStationId)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("지하철 노선의 중간 역을 포함하는 구간(중간 구간)을 제거한다.")
    void deleteBetweenSection() {
        /* given */
        final Long lineId = 3L;
        final Long betweenStationId = 37L;

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all().queryParam("stationId", betweenStationId)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("지하철 노선에 구간이 하나인 경우 삭제 시 400 Bad Request로 응답한다.")
    void badRequestWithOnlyOneSection() {
        /* given */
        final Long lineIdIsSectionsSizeOne = 1L;
        final Long stationId = 12L;

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all().queryParam("stationId", stationId)
                .when().delete("/lines/{lineId}/sections", lineIdIsSectionsSizeOne)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("해당 노선에 구간이 하나여서 제거할 수 없습니다.");
    }

    @Test
    @DisplayName("제거하려는 역이 해당 구간에 없는 경우 400 Bad Request로 응답한다.")
    void badRequestWithSectionsHasNotStation() {
        /* given */
        final Long lineId = 3L;
        final Long notContainsStationId = 30L;

        /* when */
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all().queryParam("stationId", notContainsStationId)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("해당 구간에는 해당 역이 존재하지 않아서 제거할 수 없습니다. 역 ID : " + notContainsStationId);
    }
}
