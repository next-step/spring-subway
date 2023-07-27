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

    @Test
    @DisplayName("출발역과 도착역이 같은 경우 400 Bad Request로 응답한다")
    void badRequestWithSameSourceAndTarget() {
        final Long sameStationId = 35L;

        final ExtractableResponse<Response> response = getReadPathAPIResponse(sameStationId, sameStationId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("출발역과 도착역이 같습니다. 역 ID : " + sameStationId);
    }

    private ExtractableResponse<Response> getReadPathAPIResponse(final Long source, final Long target) {
        return RestAssured.given().log().all().queryParam("source", source).queryParam("target", target)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }
}
