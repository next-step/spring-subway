package subway.dto;

import org.springframework.util.Assert;

public class StationRequest {

    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        Assert.hasText(name, "이름을 입력해야 합니다.");
        Assert.isTrue(name.length() <= 255, "이름 길이는 255자를 초과할 수 없습니다.");

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
