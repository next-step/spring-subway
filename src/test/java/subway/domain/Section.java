package subway.domain;

public class Section {
    private final int distance;

    public Section(int distance) {
        validatePositive(distance);
        this.distance = distance;
    }

    private void validatePositive(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("구간 길이는 양수여야합니다 distance: \"" + distance + "\"");
        }
    }
}
