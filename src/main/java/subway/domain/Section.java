package subway.domain;

public class Section {
    private final Long id;
    private final Station upward;
    private final Station downward;
    private final Line line;
    private final int distance;

    public Section(final Long id, final Station upward, final Station downward, final Line line, final int distance) {
        this.id = id;
        this.upward = upward;
        this.downward = downward;
        this.line = line;
        this.distance = distance;
    }

    public Section(final Station upward, final Station downward, final Line line, final int distance) {
        this(null, upward, downward, line, distance);
    }

    public Long getId() {
        return id;
    }

    public Station getDownward() {
        return downward;
    }

    public Station getUpward() {
        return upward;
    }

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }

}
