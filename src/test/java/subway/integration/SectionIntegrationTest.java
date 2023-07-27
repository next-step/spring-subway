package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dao.SectionDao;
import subway.dto.request.SectionRegistRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionIntegrationTest extends IntegrationTest {

    @Autowired
    private SectionDao sectionDao;

    @BeforeEach
    public void setUp() {
        super.setUp();

        SectionRegistRequest 구간_신중동역_춘의역 = new SectionRegistRequest(2L, 3L, 10);
        SectionRegistRequest 구간_춘의역_부천종합운동장역 = new SectionRegistRequest(3L, 4L, 10);

        Map<String, String> 온수역_등록_요청 = new HashMap<>();
        온수역_등록_요청.put("name", "온수역");
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(온수역_등록_요청)
            .when().post("/stations")
            .then().log().all();
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(구간_신중동역_춘의역)
            .when().post("/lines/1/sections")
            .then().log().all();
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(구간_춘의역_부천종합운동장역)
            .when().post("/lines/1/sections")
            .then().log().all();
    }

    @Test
    @DisplayName("구간 등록 성공 테스트")
    void create() {
        // when
        SectionRegistRequest 구간_춘의역_온수역 = new SectionRegistRequest(3L, 5L, 9);

        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(구간_춘의역_온수역)
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간 등록 성공 테스트")
    void registerSectionInMiddle() {
        // when
        SectionRegistRequest 구간_부천종합운동장역_온수역 = new SectionRegistRequest(4L, 5L, 10);

        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(구간_부천종합운동장역_온수역)
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간 상행 종점역 제거 테스트")
    void deleteUpPointStation() {
        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/1/sections?stationId=1")
            .then().log().all()
            .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        System.out.println(sectionDao.findAllByLineId(1L));
    }

    @Test
    @DisplayName("구간 하행 종점역 제거 테스트")
    void deleteDownPointStation() {
        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/1/sections?stationId=4")
            .then().log().all()
            .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        System.out.println(sectionDao.findAllByLineId(1L));
    }


    @Test
    @DisplayName("구간 중간역 제거 테스트")
    void deleteMiddleStation() {
        // when
        ExtractableResponse<Response> result = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/1/sections?stationId=3")
            .then().log().all()
            .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        System.out.println(sectionDao.findAllByLineId(1L));
    }
}
