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

        /* when */
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/1/sections")
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
        SectionRequest sectionRequest = new SectionRequest(13L, 14L, 777L);

        /* when */
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/1/sections")
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("새로운 구간의 하행역이 해당 노선에 등록되어 있는 경우 400 Bad Request로 응답한다.")
    void badRequestWithRegisteredPrevSectionOnLine() {
        /* given */
        SectionRequest sectionRequest = new SectionRequest(11L, 12L, 777L);

        /* when */
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/1/sections")
                .then().log().all()
                .extract();

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
