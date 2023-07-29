package subway.dto.request;

public class PathFindRequest {
    private Long source;
    private Long target;

    public PathFindRequest(final Long source, final Long target) {
        this.source = source;
        this.target = target;
    }

    public PathFindRequest() {
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }
}
