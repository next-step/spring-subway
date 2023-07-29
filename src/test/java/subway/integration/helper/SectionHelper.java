package subway.integration.helper;

import subway.dto.request.SectionCreateRequest;

import static subway.integration.helper.RestHelper.post;

public class SectionHelper {

    private SectionHelper() {
        throw new UnsupportedOperationException();
    }

    public static void createSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        final SectionCreateRequest request = new SectionCreateRequest(upStationId, downStationId, distance);

        post(request, "/lines/{lineId}/sections", lineId);
    }
}
