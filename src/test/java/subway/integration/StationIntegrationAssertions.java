package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import subway.dto.StationResponse;
import subway.util.ErrorTemplate;

class StationIntegrationAssertions {

    private static final String DUPLICATED_STATION = "STATION-SERVICE-401";

    private StationIntegrationAssertions() {
        throw new UnsupportedOperationException("Cannot invoke constructor \"StationIntegrationAssertions()\"");
    }

    static void assertIsDuplicatedStationName(ExtractableResponse<Response> response) {
        HttpStatusAssertions.assertIsBadRequest(response);

        ErrorTemplate errorTemplate = response.as(ErrorTemplate.class);
        assertThat(errorTemplate.getStatus()).isEqualTo(DUPLICATED_STATION);
    }

    static void assertIsStationCreated(ExtractableResponse<Response> response, StationResponse expected) {
        HttpStatusAssertions.assertIsCreated(response);

        assertStationResponse(response, expected);
    }

    static void assertIsStationFound(ExtractableResponse<Response> response, StationResponse... expected) {
        HttpStatusAssertions.assertIsOk(response);

        List<StationResponse> stationResponses = response.as(new TypeRef<>() {
        });
        assertThat(stationResponses).containsExactly(expected);
    }

    static void assertIsStationFound(ExtractableResponse<Response> response, StationResponse expected) {
        HttpStatusAssertions.assertIsOk(response);

        assertStationResponse(response, expected);
    }

    private static void assertStationResponse(ExtractableResponse<Response> response, StationResponse expected) {
        StationResponse stationResponse = response.as(StationResponse.class);
        assertThat(stationResponse).isEqualTo(expected);
    }

}
