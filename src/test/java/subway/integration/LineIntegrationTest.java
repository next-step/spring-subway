package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import subway.domain.dto.LineDto;
import subway.web.dto.LineRequest;
import subway.web.dto.LineResponse;
import subway.web.dto.SectionRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@Sql(value = {"/truncate.sql", "/data.sql"}
        , config = @SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED))
public class LineIntegrationTest extends IntegrationTest {
    private LineRequest lineRequest1;
    private LineRequest lineRequest2;
    private SectionRequest sectionRequest1;
    private SectionRequest sectionRequest2;

    public LineIntegrationTest() {
        lineRequest1 = new LineRequest("신분당선", "bg-red-600");
        lineRequest2 = new LineRequest("구신분당선", "bg-red-600");

        sectionRequest1 = SectionRequest.builder()
                .downStationId(2L)
                .upStationId(1L)
                .distance(10)
                .build();
        sectionRequest2 = SectionRequest.builder()
                .downStationId(3L)
                .upStationId(2L)
                .distance(5)
                .build();
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 그래프 초기화
        RestAssured
                .given().log().all()
                .when()
                .get("/lines/sections/init")
                .then().log().all()
                .statusCode(200);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("특정 지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 그래프 초기화
        RestAssured
                .given().log().all()
                .when()
                .get("/lines/sections/init")
                .then().log().all()
                .statusCode(200);

        Long lineId = 1L;

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineDto lineDto = response.as(LineDto.class);
        assertThat(lineDto.getId()).isEqualTo(lineId);

        // TODO: 궁금한점
        // data.sql 로 DB 초기값을 세팅하도록 되어 있는데,
        // 목록 조회의 테스트 같은 경우, 상세 값 비교로 검증을 하면, 초기 데이터가 변경되는 경우에 테스트가 틀어질 것 같은데 혹시 방법이 있나요?
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest2)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("노선에 구간(역)을 등록합니다")
    void addStationToLine() {
        // given
        Long lineId = 1L;
        Long downStationId = 2L;
        Long upStationId = 2L;
        Integer distance = 10;

        SectionRequest sectionRequest = SectionRequest.builder()
                .downStationId(downStationId)
                .upStationId(upStationId)
                .distance(distance)
                .build();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .pathParam("id", lineId)
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("노선에 있는 구간(역)을 제거합니다")
    void removeStationToLine() {

        Long lineId = 1L;
        Long stationId = 4L;    // 해당 호선의 하행 종점역 이어야 합니다

        // 그래프 초기화
        RestAssured
                .given().log().all()
                .when()
                .get("/lines/sections/init")
                .then().log().all()
                .statusCode(200);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .pathParam("lineId", lineId)
                .queryParam("stationId", stationId)
                .when()
                .delete("/lines/{lineId}/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(204);
    }
}
