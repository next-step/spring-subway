package subway.integration.helper;

import subway.dto.request.LineCreateRequest;

import static subway.integration.helper.RestHelper.post;

public class LineHelper {

    private LineHelper() {
        throw new UnsupportedOperationException();
    }

    // TODO: 접근 제어자?
    // TODO: color 순서 변경?
    public static void createLine(
            final String name,
            final Long upStationId,
            final Long downStationId,
            final int distance,
            final String color
    ) {
        final LineCreateRequest request = new LineCreateRequest(name, upStationId, downStationId, distance, color);

        post(request, "/lines");
    }
}
