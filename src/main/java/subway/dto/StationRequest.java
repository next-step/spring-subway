package subway.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class StationRequest {

    @NotNull(message = "지하철 역 이름은 null 일 수 없습니다.")
    @NotBlank(message = "지하철 역 이름은 비어있을 수 없습니다.")
    @Size(message = "지하철 역 이름의 길이는 1 ~ 255 사이 입니다.", min = 1, max = 255)
    private String name;

    public StationRequest() {
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
