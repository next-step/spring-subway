package subway.domain;

public class Section {
    private final Station upward;
    private final Station downward;
    private final int distance;

    public Section(Station upward, Station downward, int distance) {
        this.upward = upward;
        this.downward = downward;
        this.distance = distance;
    }

    public Station getDownward() {
        return this.downward;
    }

    public Station getUpward() {
        return upward;
    }
}
