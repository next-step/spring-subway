package subway.dto;

import javax.validation.constraints.NotNull;

public class PathRequest {

    @NotNull
    private final Long source;
    @NotNull
    private final Long target;

    public PathRequest(final Long source, final Long target) {
        this.source = source;
        this.target = target;
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }
}
