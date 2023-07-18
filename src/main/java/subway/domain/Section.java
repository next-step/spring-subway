package subway.domain;

public class Section {
    private final Long id;
    private final Station upward;
    private final Station downward;
    private final Line line;
    private final int distance;

    public Section(final Long id, final Station upward, final Station downward, final Line line, final int distance) {
        validate(upward, downward);
        
        this.id = id;
        this.upward = upward;
        this.downward = downward;
        this.line = line;
        this.distance = distance;
    }

    public Section(final Station upward, final Station downward, final Line line, final int distance) {
        this(null, upward, downward, line, distance);
    }

    private void validate(Station upward, Station downward) {
        if (upward == downward) {
            throw new IllegalArgumentException("상행역과 하행역이 같을 수 없습니다");
        }
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
