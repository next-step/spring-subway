package subway.domain;

public class Section {
    private final Long id;
    private final Station upward;
    private final Station downward;
    private final Line line;
    private final int distance;

    public Section(Long id, Station upward, Station downward, Line line, int distance) {
        this.id = id;
        this.upward = upward;
        this.downward = downward;
        this.line = line;
        this.distance = distance;
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
