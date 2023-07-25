package subway.dto;

import javax.validation.constraints.NotBlank;

public class CreateStationRequest {

    @NotBlank(message = "역 이름을 입력해주세요.")
    private String name;

    public CreateStationRequest() {
    }

    public CreateStationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
