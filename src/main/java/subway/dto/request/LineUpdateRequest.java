package subway.dto.request;

import javax.validation.constraints.NotEmpty;

public class LineUpdateRequest {

    @NotEmpty
    private String name;
    @NotEmpty
    private String color;

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
