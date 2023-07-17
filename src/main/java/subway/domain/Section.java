package subway.domain;

public class Section {
    private final String upward;
    private final String downward;
    private final int distance;

    public Section(String upward, String downward, int distance) {
        this.upward = upward;
        this.downward = downward;
        this.distance = distance;
    }
}
