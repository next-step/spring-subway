package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PathIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("정상적으로 경로를 찾아서 응답한다.")
    void getPath() {
        final Long source = 35L;
        final Long target = 37L;

        final ExtractableResponse<Response> response = getReadPathAPIResponse(source, target);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> getReadPathAPIResponse(final Long source, final Long target) {
        return RestAssured.given().log().all().queryParam("source", source).queryParam("target", target)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
