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
import subway.dto.SectionRequest;

@DisplayName("지하철 구간 관련 기능")
public class SectionIntegrationTest extends IntegrationTest {

    private Long lineId;
    private SectionRequest sectionRequest1;
    private SectionRequest sectionRequest2;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lineId = 1001L;
        sectionRequest1 = new SectionRequest(1001L, 1002L, 10L);
        sectionRequest2 = new SectionRequest(1002L, 1003L, 10L);
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }


}
