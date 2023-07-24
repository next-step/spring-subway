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
import subway.dto.request.SectionRegistRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionIntegrationTest extends IntegrationTest {

    private SectionRegistRequest sectionRegistRequest1;
    private SectionRegistRequest sectionRegistRequest2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        sectionRegistRequest1 = new SectionRegistRequest(2L, 3L, 10);
        sectionRegistRequest2 = new SectionRegistRequest(3L, 4L, 10);
    }

    @Test
    @DisplayName("구간 등록 성공 테스트")
    void create() {
        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegistRequest1)
            .when().post("/lines/1/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("구간 제거 테스트")
    void delete() {
        // given
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegistRequest1)
            .when().post("/lines/1/sections")
            .then().log().all();

        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(sectionRegistRequest2)
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
}
