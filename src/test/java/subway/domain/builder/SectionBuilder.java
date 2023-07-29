package subway.domain.builder;

import subway.domain.Distance;
import subway.domain.Section;

public class SectionBuilder {

    private SectionBuilder() {
        /* no-op */
    }

    public static Section createSection(final Long upStationId, final Long downStationId) {
        return createSection(1L, upStationId, downStationId);
    }

    public static Section createSection(final Long lineId, final Long upStationId, final Long downStationId) {
        return new Section(lineId, upStationId, downStationId, new Distance(1));
    }
}
