package subway.integration.helper;

import subway.dto.LineRequest;

public class LineHelper extends RestHelper {

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
        final LineRequest request = new LineRequest(name, upStationId, downStationId, distance, color);
        
        post(request, "/lines");
    }
}
