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

    static void assertIsLineCreated(ExtractableResponse<Response> response, LineResponse expected) {
        HttpStatusAssertions.assertIsCreated(response);

        assertLineResponse(response, expected);
    }

    static void assertIsLineFound(ExtractableResponse<Response> response, LineResponse expected) {
        HttpStatusAssertions.assertIsOk(response);

        assertLineResponse(response, expected);
    }

    private static void assertLineResponse(ExtractableResponse<Response> response, LineResponse expected) {
        LineResponse lineResponse = response.as(LineResponse.class);
        assertThat(lineResponse).isEqualTo(expected);
    }

    static void assertIsLineFound(ExtractableResponse<Response> response, LineResponse... exactlyExpected) {
        HttpStatusAssertions.assertIsOk(response);

        List<LineResponse> lineResponses = response.as(new TypeRef<>() {
        });
        assertThat(lineResponses).containsExactly(exactlyExpected);
    }

}
