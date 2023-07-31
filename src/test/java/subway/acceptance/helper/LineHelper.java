package subway.acceptance.helper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.dto.request.LineCreateRequest;

import static subway.acceptance.helper.RestHelper.post;

public class LineHelper {

    private LineHelper() {
        throw new UnsupportedOperationException();
    }

    public static ExtractableResponse<Response> createLine(
            final String name,
            final String color,
            final Long upStationId,
            final Long downStationId,
            final int distance
    ) {
        final LineCreateRequest request = new LineCreateRequest(name, color, upStationId, downStationId, distance);

        return post(request, "/lines");
    }
}
