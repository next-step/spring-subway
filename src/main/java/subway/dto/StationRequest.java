package subway.dto;

import javax.validation.constraints.NotBlank;

public class StationRequest {
    @NotBlank(message = "역 이름은 공백이거나 비어있을 수 없습니다")
    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
