package subway.domain.fixture;

import subway.domain.Line;

public class LineFixture {
    public static Line createDefaultLine() {
        return new Line("2호선", "green");
    }

    public static Line createLine(String name) {
        return new Line(name, "green");
    }
}
