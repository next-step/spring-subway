package subway.fixture;

import subway.domain.Line;
import subway.domain.Section;

public class SectionFixture {

    public static final int DEFAULT_DISTANCE = 10;

    public static Section 첫번째역_두번째역_구간(final Line line) {
        return new Section(1L, line, StationFixture.첫번째역(), StationFixture.두번째역(), DEFAULT_DISTANCE);
    }

    public static Section 두번째역_세번째역_구간(final Line line) {
        return new Section(2L, line, StationFixture.두번째역(), StationFixture.세번째역(), DEFAULT_DISTANCE);
    }

    public static Section 세번째역_네번째역_구간(final Line line) {
        return new Section(3L, line, StationFixture.세번째역(), StationFixture.네번째역(), DEFAULT_DISTANCE);
    }

    public static Section 네번째역_다섯번째역_구간(final Line line) {
        return new Section(4L, line, StationFixture.네번째역(), StationFixture.다섯번째역(), DEFAULT_DISTANCE);
    }

    public static Section 다섯번째역_여섯번째역_구간(final Line line) {
        return new Section(5L, line, StationFixture.다섯번째역(), StationFixture.여섯번째역(), DEFAULT_DISTANCE);
    }

    public static Section 여섯번째역_일곱번째역_구간(final Line line) {
        return new Section(6L, line, StationFixture.여섯번째역(), StationFixture.일곱번째역(), DEFAULT_DISTANCE);
    }
}
