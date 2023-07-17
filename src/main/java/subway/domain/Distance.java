package subway.domain;

public class Distance {

    private final int distance;

    public Distance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    private void validateDistance(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0 이하일 수 없습니다.");
        }
    }
}
