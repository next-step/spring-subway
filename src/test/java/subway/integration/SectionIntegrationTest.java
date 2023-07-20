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

    private StationRequest stationRequest;
    private SectionRequest sectionRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();

        stationRequest = new StationRequest("서울");
        sectionRequest = new SectionRequest( "2", "3",10);
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        createInitialLine();

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest)
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

        ExtractableResponse<Response> responseForCheck = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/1")
                .then().log().all()
                .extract();

        assertThat(responseForCheck.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 노선으로 인한 구간 생성 실패")
    @Test
    void createSectionWithUnmatchedLineId() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있어서 구간 생성 실패")
    @Test
    void createSectionWithBothExistingStation() {
        // given
        createInitialLine();
        final SectionRequest badSectionRequest = new SectionRequest("1", "2", 5);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(badSectionRequest)
                .when().post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 모두 노선에 등록되어 있지 않아서 구간 생성 실패")
    @Test
    void createSectionWithBothNotExistingStation() {
        // given
        createInitialLine();
        final SectionRequest badSectionRequest = new SectionRequest("5", "6", 5);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(badSectionRequest)
                .when().post("/lines/1/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        createInitialLine();

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest)
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

    @DisplayName("지하철 노선의 하행 종점역이 아닐 때 구간 제거 실패")
    @Test
    void deleteSectionWithNotLastStation() {
        // given
        createInitialLine();

        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest)
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
                .when().delete("/lines/1/sections?stationId=1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선에 구간이 1개일 때 구간 제거 실패")
    @Test
    void deleteSectionAtLineHasOneSection() {
        // given
        createInitialLine();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/1/sections?stationId=2")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void createInitialLine() {
        final LineRequest lineRequest = new LineRequest("1호선", 1L, 2L, 10, "blue");
        final StationRequest stationRequest1 = new StationRequest("인천");
        final StationRequest stationRequest2 = new StationRequest("부평");

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
    }
}
