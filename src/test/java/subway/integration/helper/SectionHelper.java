package subway.integration.helper;

import subway.dto.SectionRequest;

public class SectionHelper extends RestHelper {

    private SectionHelper() {
        throw new UnsupportedOperationException();
    }

    public static void createSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        final SectionRequest request = new SectionRequest(upStationId, downStationId, distance);

        post(request, "/lines/{lineId}/sections", lineId);
    }
}
