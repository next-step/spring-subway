package subway.dto;

public class PathRequest {

    private Long sourceId;
    private Long targetId;

    public PathRequest(final Long sourceId, final Long targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public Long getTargetId() {
        return targetId;
    }
}
