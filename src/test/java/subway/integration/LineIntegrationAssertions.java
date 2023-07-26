package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.domain.status.LineExceptionStatus.DUPLICATED_SECTIONS;
import static subway.domain.status.SectionExceptionStatus.CANNOT_DISCONNECT_SECTION;
import static subway.domain.status.SectionExceptionStatus.ILLEGAL_DISTANCE;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import subway.dto.LineResponse;
import subway.util.ErrorTemplate;

class LineIntegrationAssertions {

    private static final String DUPLICATE_LINE = "LINE-SERVICE-403";

    private LineIntegrationAssertions() {
        throw new UnsupportedOperationException("Cannot invoke constructor \"LineIntegrationAssertions()\"");
    }

    static void assertIsDuplicateLineName(ExtractableResponse<Response> response) {
        HttpStatusAssertions.assertIsBadRequest(response);

        ErrorTemplate errorTemplate = response.as(ErrorTemplate.class);
        assertThat(errorTemplate.getStatus()).isEqualTo(DUPLICATE_LINE);
    }

    static void assertIsIllegalDistance(ExtractableResponse<Response> response) {
        HttpStatusAssertions.assertIsBadRequest(response);

        ErrorTemplate errorTemplate = response.as(ErrorTemplate.class);
        assertThat(errorTemplate.getStatus()).isEqualTo(ILLEGAL_DISTANCE.getStatus());
    }

    static void assertIsDuplicateSection(ExtractableResponse<Response> response) {
        HttpStatusAssertions.assertIsBadRequest(response);

        ErrorTemplate errorTemplate = response.as(ErrorTemplate.class);
        assertThat(errorTemplate.getStatus()).isEqualTo(DUPLICATED_SECTIONS.getStatus());
    }

    static void assertIsCannotDisconnectSection(ExtractableResponse<Response> response) {
        HttpStatusAssertions.assertIsBadRequest(response);

        ErrorTemplate errorTemplate = response.as(ErrorTemplate.class);
        assertThat(errorTemplate.getStatus()).isEqualTo(CANNOT_DISCONNECT_SECTION.getStatus());
    }

    static void assertIsLineCreated(ExtractableResponse<Response> response) {
        HttpStatusAssertions.assertIsCreated(response);

        LineResponse lineResponse = response.as(LineResponse.class);
        assertLineResponse(lineResponse);
    }

    static void assertIsLineFound(ExtractableResponse<Response> response, int expectedSize) {
        HttpStatusAssertions.assertIsOk(response);

        List<LineResponse> lineResponses = response.as(new TypeRef<>() {
        });
        assertThat(lineResponses).hasSize(expectedSize);
        lineResponses.forEach(LineIntegrationAssertions::assertLineResponse);
    }

    static void assertIsLineFound(ExtractableResponse<Response> response) {
        HttpStatusAssertions.assertIsOk(response);

        LineResponse lineResponse = response.as(LineResponse.class);
        LineIntegrationAssertions.assertLineResponse(lineResponse);
    }

    private static void assertLineResponse(LineResponse lineResponse) {
        assertThat(lineResponse.getId()).isInstanceOf(Number.class);
        assertThat(lineResponse.getName()).isNotEmpty().isInstanceOf(String.class);
        assertThat(lineResponse.getColor()).isNotEmpty().isInstanceOf(String.class);
        assertThat(lineResponse.getStations()).isNotEmpty().hasSizeGreaterThanOrEqualTo(2);
    }

}
