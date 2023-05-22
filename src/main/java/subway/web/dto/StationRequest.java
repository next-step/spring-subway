package subway.web.dto;

import javax.validation.constraints.NotBlank;

public class StationRequest {
    @NotBlank
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }
}
