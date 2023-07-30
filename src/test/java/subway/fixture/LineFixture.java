package subway.fixture;

import subway.domain.Line;

public class LineFixture {

    public static final String DEFAULT_COLOR = "#FFFFFF";

    public static Line 신분당선() {
        return new Line(1L, "신분당선", LineFixture.DEFAULT_COLOR);
    }

    public static Line 이호선() {
        return new Line(2L, "이호선", LineFixture.DEFAULT_COLOR);
    }

    public static Line 사호선() {
        return new Line(3L, "사호선", LineFixture.DEFAULT_COLOR);
    }

    public static Line 구호선() {
        return new Line(4L, "구호선", LineFixture.DEFAULT_COLOR);
    }
}
