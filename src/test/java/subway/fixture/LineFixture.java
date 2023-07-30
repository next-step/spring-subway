package subway.fixture;

import subway.domain.Line;

public class LineFixture {

    public static final String DEFAULT_COLOR = "#FFFFFF";

    public static Line 신분당선() {
        return new Line(1L, "신분당선", LineFixture.DEFAULT_COLOR);
    }

    public static Line 구신분당선() {
        return new Line(2L, "구신분당선", LineFixture.DEFAULT_COLOR);
    }
}
