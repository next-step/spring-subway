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

    public static Section 범계역_경마공원역_구간(final int distance) {
        return new Section(7L, LineFixture.사호선(), StationFixture.범계역(), StationFixture.경마공원역(), distance);
    }

    public static Section 경마공원역_사당역_구간(final int distance) {
        return new Section(8L, LineFixture.사호선(), StationFixture.경마공원역(), StationFixture.사당역(), distance);
    }

    public static Section 사당역_신용산역_구간(final int distance) {
        return new Section(9L, LineFixture.사호선(), StationFixture.사당역(), StationFixture.신용산역(), distance);
    }

    public static Section 경마공원역_강남역_구간(final int distance) {
        return new Section(10L, LineFixture.신분당선(), StationFixture.경마공원역(), StationFixture.강남역(), distance);
    }

    public static Section 사당역_강남역_구간(final int distance) {
        return new Section(11L, LineFixture.이호선(), StationFixture.사당역(), StationFixture.강남역(), distance);
    }

    public static Section 강남역_잠실역_구간(final int distance) {
        return new Section(12L, LineFixture.이호선(), StationFixture.강남역(), StationFixture.잠실역(), distance);
    }

    public static Section 여의도역_노량진역_구간(final int distance) {
        return new Section(12L, LineFixture.구호선(), StationFixture.여의도역(), StationFixture.노량진역(), distance);
    }
}
