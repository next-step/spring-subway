package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.HttpStatusAssertions.assertIsBadRequest;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import subway.domain.status.PathExceptionStatus;
import subway.dto.PathFindResponse;
import subway.util.ErrorTemplate;

class PathIntegrationAssertions {

    private static final String CANNOT_FIND_STATION = "PATH-SERVICE-401";

    private PathIntegrationAssertions() {
        throw new UnsupportedOperationException("Cannot invoke constructor \"PathIntegrationAssertions()\"");
    }

    static void assertStationPath(ExtractableResponse<Response> response, int distance,
        PathFindResponse.StationResponse... exactlyExpected) {

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        PathFindResponse pathFindResponse = response.as(PathFindResponse.class);
        assertThat(pathFindResponse.getDistance()).isEqualTo(distance);
        assertThat(pathFindResponse.getStations()).containsExactly(exactlyExpected);
    }

    static void assertIsNotExistStation(ExtractableResponse<Response> response) {
        assertIsBadRequest(response);

        ErrorTemplate errorTemplate = response.as(ErrorTemplate.class);
        assertThat(errorTemplate.getStatus()).isEqualTo(CANNOT_FIND_STATION);
    }

    static void assertIsStationNotContainedPath(ExtractableResponse<Response> response) {
        assertIsBadRequest(response);

        ErrorTemplate errorTemplate = response.as(ErrorTemplate.class);
        assertThat(errorTemplate.getStatus()).isEqualTo(PathExceptionStatus.CANNOT_FIND_PATH.getStatus());
    }

}
