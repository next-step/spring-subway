package subway.dto;

import org.springframework.util.Assert;

public class StationRequest {
    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        Assert.notNull(name, "이름을 입력해야 합니다.");

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
