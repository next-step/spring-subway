package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    private LineRequest lineRequest;
    private StationRequest stationRequest1;
    private StationRequest stationRequest2;
    private StationRequest stationRequest3;
    private SectionRequest sectionRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lineRequest = new LineRequest("1호선", 1L, 2L, 10, "blue");
        stationRequest1 = new StationRequest("인천");
        stationRequest2 = new StationRequest("부평");
        stationRequest3 = new StationRequest("서울");
        sectionRequest = new SectionRequest( "2", "3",10);
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest1)
                .when().post("/stations")
                .then().log().all()
                .extract();

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest2)
                .when().post("/stations")
                .then().log().all()
                .extract();

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest3)
                .when().post("/stations")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest1)
                .when().post("/stations")
                .then().log().all()
                .extract();

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest2)
                .when().post("/stations")
                .then().log().all()
                .extract();

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest3)
                .when().post("/stations")
                .then().log().all()
                .extract();

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/1/sections")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/1/sections?stationId=3")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
