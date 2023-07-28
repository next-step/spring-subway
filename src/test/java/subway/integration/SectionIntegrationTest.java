package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.request.LineRequest;
import subway.dto.request.SectionRegisterRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionIntegrationTest extends IntegrationTest {

    private SectionRegisterRequest sectionRegisterRequest1;
    private SectionRegisterRequest sectionRegisterRequest2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        sectionRegisterRequest1 = new SectionRegisterRequest(2L, 3L, 10);
        sectionRegisterRequest2 = new SectionRegisterRequest(3L, 4L, 10);
    }

    @Test
    @DisplayName("구간 등록 성공 테스트: 노선에 아무 역도 없는 경우")
    void create1() {
        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegisterRequest1)
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간 등록 성공 테스트: 중간에 추가")
    void create2() {
        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionRegisterRequest(2L, 4L, 5))
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간 등록 실패 테스트: 중복되는 구간 추가")
    void createException1() {
        // given
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionRegisterRequest(1L, 2L, 10))
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionRegisterRequest(1L, 2L, 10))
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 등록 실패 테스트: 기존 노선에 없는 구간 추가")
    void createException2() {
        // given
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionRegisterRequest(1L, 2L, 10))
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionRegisterRequest(3L, 4L, 10))
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 제거 테스트: 맨 끝 구간 삭제")
    void delete1() {
        // given
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegisterRequest1)
            .when().post("/lines/1/sections")
            .then().log().all();

        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegisterRequest2)
            .when().post("/lines/1/sections")
            .then().log().all();

        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/1/sections?stationId=4")
            .then().log().all()
            .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("구간 제거 테스트: 중간 구간 삭제")
    void delete2() {
        // given
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegisterRequest1)
            .when().post("/lines/1/sections")
            .then().log().all();

        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegisterRequest2)
            .when().post("/lines/1/sections")
            .then().log().all();

        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/1/sections?stationId=3")
            .then().log().all()
            .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("구간 제거 예외 테스트: 구간이 1개인 경우 삭제 실패")
    void deleteException1() {
        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/1/sections?stationId=2")
            .then().log().all()
            .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("구간 제거 예외 테스트: 구간에 없는 역을 제거하려고 하면 실패")
    void deleteException2() {
        // given
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionRegisterRequest(2L, 3L, 10))
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/1/sections?stationId=4")
            .then().log().all()
            .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("경로 조회 테스트")
    void findPath() {
        // given
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegisterRequest1)
            .when().post("/lines/1/sections")
            .then().log().all();

        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegisterRequest2)
            .when().post("/lines/1/sections")
            .then().log().all();

        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/path?source=1&target=4")
            .then().log().all()
            .extract();

        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("경로 조회 예외 테스트: 두 역이 연결되어 있지 않은 경우")
    void findPathException1() {
        // given
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new LineRequest("일호선", "파랑", 3L, 4L, 10))
            .when().post("/lines")
            .then().log().all();

        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/path?source=1&target=4")
            .then().log().all()
            .extract();

        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("경로 조회 예외 테스트: 출발역과 도착역이 동일한 경우")
    void findPathException2() {
        // given
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegisterRequest1)
            .when().post("/lines/1/sections")
            .then().log().all();

        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/path?source=1&target=1")
            .then().log().all()
            .extract();

        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("경로 조회 예외 테스트: 존재하지 않은 역을 입력한 경우")
    void findPathException3() {
        // given
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegisterRequest1)
            .when().post("/lines/1/sections")
            .then().log().all();

        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/path?source=7&target=8")
            .then().log().all()
            .extract();

        //then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
