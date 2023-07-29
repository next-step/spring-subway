package subway.acceptance.helper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.dto.request.SectionCreateRequest;

import java.util.List;

import static subway.acceptance.helper.RestHelper.post;

public class SectionHelper {

    private SectionHelper() {
        throw new UnsupportedOperationException();
    }

    public static ExtractableResponse<Response> createSection(
            final Long lineId,
            final Long upStationId,
            final Long downStationId,
            final int distance
    ) {
        final SectionCreateRequest request = new SectionCreateRequest(upStationId, downStationId, distance);

        return post(request, "/lines/{lineId}/sections", List.of(lineId));
    }
}
