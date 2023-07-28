package subway.ui.dto;

import org.springframework.util.Assert;

public class SectionRequest {

    private long upStationId;
    private long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(final String upStationId, final String downStationId, final int distance) {
        Assert.hasText(upStationId, "상행역을 입력해야 합니다.");
        Assert.hasText(downStationId, "하행역을 입력해야 합니다.");
        Assert.isTrue(distance > 0, "구간 길이는 0보다 커야합니다.");

        this.upStationId = Long.parseLong(upStationId);
        this.downStationId = Long.parseLong(downStationId);
        this.distance = distance;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
