package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.ErrorResponse;
import subway.dto.SectionRequest;
import subway.helper.CreateHelper;
import subway.helper.GetHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @DisplayName("하행 종점역을 연장하는 지하철 구간을 생성한다.")
    @Test
    void createDownwardSection() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        SectionRequest sectionRequest = new SectionRequest(yeoksamId, gyodaeId, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> stationNames = GetHelper.getStationNamesInLine(lineId);
        assertThat(stationNames).containsExactly("강남역", "역삼역", "교대역");
    }

    @DisplayName("상행 종점역을 연장하는 지하철 구간을 생성한다.")
    @Test
    void createUpwardSection() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        SectionRequest sectionRequest = new SectionRequest(gyodaeId, gangnamId, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> stationNames = GetHelper.getStationNamesInLine(lineId);
        assertThat(stationNames).containsExactly("교대역", "강남역", "역삼역");
    }

    @DisplayName("상행역을 기준으로 중간에 역을 삽입하여 지하철 구간을 생성한다.")
    @Test
    void createUpInsertSection() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        SectionRequest sectionRequest = new SectionRequest(gangnamId, gyodaeId, 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> stationNames = GetHelper.getStationNamesInLine(lineId);
        assertThat(stationNames).containsExactly("강남역", "교대역", "역삼역");
    }

    @DisplayName("하행역을 기준으로 중간에 역을 삽입하여 지하철 구간을 생성한다.")
    @Test
    void createDownInsertSection() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        SectionRequest sectionRequest = new SectionRequest(gyodaeId, yeoksamId, 5);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> stationNames = GetHelper.getStationNamesInLine(lineId);
        assertThat(stationNames).containsExactly("강남역", "교대역", "역삼역");
    }

    @DisplayName("기존에 존재하는 지하철 역으로 지하철 구간을 생성한다.")
    @Test
    void createAlreadyExist() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        SectionRequest sectionRequest = new SectionRequest(yeoksamId, gangnamId, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.as(ErrorResponse.class).getCode())
                .isEqualTo("SECTION_001");
    }

    @DisplayName("기존에 존재하지 않는 지하철 역으로 지하철 구간을 생성한다.")
    @Test
    void createNotExist() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long seonreungId = CreateHelper.createStation("선릉역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        SectionRequest sectionRequest = new SectionRequest(gyodaeId, seonreungId, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.as(ErrorResponse.class).getCode())
                .isEqualTo("SECTION_001");
    }

    @DisplayName("같은 역으로 지하철 구간을 생성한다.")
    @Test
    void createSame() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        SectionRequest sectionRequest = new SectionRequest(gangnamId, gangnamId, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.as(ErrorResponse.class).getCode())
                .isEqualTo("SECTION_009");
    }

    @DisplayName("중간에 기존 구간보다 길이가 긴 지하철 구간을 생성한다.")
    @Test
    void createTooLongSection() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        SectionRequest sectionRequest = new SectionRequest(gangnamId, gyodaeId, 10);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.as(ErrorResponse.class).getCode())
                .isEqualTo("SECTION_012");
    }

    @DisplayName("종점역 지하철 구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        CreateHelper.createSection(yeoksamId, gyodaeId, 10, lineId);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, gyodaeId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        List<String> stationNames = GetHelper.getStationNamesInLine(lineId);
        assertThat(stationNames).containsExactly("강남역", "역삼역");
    }

    @DisplayName("중간에 있는 지하철 구간을 제거한다.")
    @Test
    void deleteMiddleStation() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        CreateHelper.createSection(yeoksamId, gyodaeId, 5, lineId);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, yeoksamId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        List<String> stationNames = GetHelper.getStationNamesInLine(lineId);
        assertThat(stationNames).containsExactly("강남역", "교대역");
    }

    @DisplayName("마지막 남은 지하철 구간을 제거한다.")
    @Test
    void deleteLastSection() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, yeoksamId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.as(ErrorResponse.class).getCode())
                .isEqualTo("SECTION_003");
    }

    @DisplayName("기존에 존재하지 않는 역으로 구간을 제거한다.")
    @Test
    void deleteNotExist() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long seonreungId = CreateHelper.createStation("선릉역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);
        CreateHelper.createSection(yeoksamId, seonreungId, 5, lineId);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, gyodaeId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.as(ErrorResponse.class).getCode())
                .isEqualTo("SECTION_004");
    }

    @DisplayName("역 id가 올바르지 않은 경우 예외 처리한다.")
    @Test
    void deleteIdNotValid() {
        // given
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yeoksamId = CreateHelper.createStation("역삼역");
        Long lineId = CreateHelper.createLine("2호선", "bg-123", gangnamId, yeoksamId, 10);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, "강남역")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.as(ErrorResponse.class).getCode())
                .isEqualTo("STATION_002");
    }
}
