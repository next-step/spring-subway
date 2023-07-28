package subway.dto.request;

import javax.validation.constraints.NotBlank;

public class UpdateStationRequest {

    @NotBlank(message = "역 이름을 입력해주세요.")
    private String name;

    private UpdateStationRequest() {
    }

    public UpdateStationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
