package subway.domain;

public class Path {

    private final Long pathId;
    private final Distance distance;

    public Path(final Long pathId, final Distance distance) {
        this.pathId = pathId;
        this.distance = distance;
    }

    public Long getPathId() {
        return pathId;
    }

    public Distance getDistance() {
        return distance;
    }
}
