package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.ExceptionResponse;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("유효성 검사 통합 테스트")
public class ValidationIntegrationTest extends IntegrationTest {
    @Test
    @DisplayName("이름이 null이면 지하철역을 생성할 수 없다.")
    void createNullStationTest() {
        // given
        StationRequest stationRequest = new StationRequest(null);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("역 이름은 공백이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("이름이 공백이면 지하철역을 생성할 수 없다.")
    void createBlankStationTest() {
        // given
        StationRequest stationRequest = new StationRequest("");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("역 이름은 공백이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("이름이 null이면 지하철역을 수정할 수 없다.")
    void updateNullStationTest() {
        // given
        StationRequest stationRequest = new StationRequest(null);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/stations/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("역 이름은 공백이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("이름이 공백이면 지하철역을 수정할 수 없다.")
    void updateBlankStationTest() {
        // given
        StationRequest stationRequest = new StationRequest("");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/stations/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("역 이름은 공백이거나 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("상행역 id가 null이면 구간을 생성할 수 없다.")
    void createNullUpStationSectionTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest(null, 2L, 1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("상행역 id는 필수 항목입니다.");
    }

    @Test
    @DisplayName("하행역 id가 null이면 구간을 생성할 수 없다.")
    void createNullDownStationSectionTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, null, 1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("하행역 id는 필수 항목입니다.");
    }

    @Test
    @DisplayName("구간 거리가 null이면 구간을 생성할 수 없다.")
    void createNullDistanceSectionTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, null);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("거리는 필수 항목입니다.");
    }

    @Test
    @DisplayName("노선 이름이 null이면 노선을 생성할 수 없다.")
    void createNullNameLineTest() {
        // given
        LineRequest lineRequest = new LineRequest(null, "red", 1L, 2L, 1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("노선 이름은 공백 또는 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("노선 이름이 공백이면 노선을 생성할 수 없다.")
    void createBlankNameLineTest() {
        // given
        LineRequest lineRequest = new LineRequest("", "red", 1L, 2L, 1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("노선 이름은 공백 또는 비어있을 수 없습니다");
    }

    @Test
    @DisplayName("노선 색상이 null이면 노선을 생성할 수 없다.")
    void createNullColorLineTest() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", null, 1L, 2L, 1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("노선 색상은 필수 항목입니다");
    }

    @Test
    @DisplayName("노선 색상이 공백이면 노선을 생성할 수 없다.")
    void createBlankColorLineTest() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "", 1L, 2L, 1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("노선 색상은 필수 항목입니다");
    }

    @Test
    @DisplayName("노선 상행종점역이 null이면 노선을 생성할 수 없다.")
    void createNullUpStationLineTest() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "red", null, 2L, 1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("상행종점역 id는 필수 항목입니다");
    }

    @Test
    @DisplayName("노선 하행종점역이 null이면 노선을 생성할 수 없다.")
    void createNullDownStationLineTest() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "red", 1L, null, 1);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("하행종점역 id는 필수 항목입니다");
    }

    @Test
    @DisplayName("노선 구간 거리가 null이면 노선을 생성할 수 없다.")
    void createNullDistanceLineTest() {
        // given
        LineRequest lineRequest = new LineRequest("2호선", "red", 1L, 2L, null);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo("거리는 필수 항목입니다");
    }
}
