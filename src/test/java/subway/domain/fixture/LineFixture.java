package subway.domain.fixture;

import subway.domain.Line;

public class LineFixture {

    public static Line createLineA() {
        return new Line(1L, "A", "red");
    }

    public static Line createLineB() {
        return new Line(2L, "B", "blue");
    }

    public static Line createLineC() {
        return new Line(3L, "C", "green");
    }

    public static Line createLineD() {
        return new Line(4L, "D", "white");
    }
}
