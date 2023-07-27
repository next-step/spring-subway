package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.StationResponse;

class PathIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("정상적으로 경로를 찾아서 응답한다.")
    void findShortestPath() {
        final Long source = 35L;
        final Long target = 37L;

        final ExtractableResponse<Response> response = getReadPathAPIResponse(source, target);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final List<Long> stationIds = response.body().jsonPath().getList("stations", StationResponse.class).stream()
                .map(StationResponse::getId).collect(Collectors.toList());
        assertThat(stationIds).containsExactly(35L, 36L, 37L);
        assertThat(response.body().jsonPath().getLong("distance")).isEqualTo(1554);
    }

    private ExtractableResponse<Response> getReadPathAPIResponse(final Long source, final Long target) {
        return RestAssured.given().log().all().queryParam("source", source).queryParam("target", target)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
