package subway.dto;

import org.springframework.util.Assert;
import subway.domain.Section;

public class SectionRequest {

    private long upStationId;
    private long downStationId;
    private int distance;

    private SectionRequest() {
    }

    public SectionRequest(final String upStationId, final String downStationId, final int distance) {
        Assert.notNull(upStationId, "상행역을 입력해야 합니다.");
        Assert.notNull(downStationId, "하행역을 입력해야 합니다.");

        this.upStationId = Long.parseLong(upStationId);
        this.downStationId = Long.parseLong(downStationId);
        this.distance = distance;
    }

    public Section to(final long lineId) {
        return new Section(lineId, upStationId, downStationId, distance);
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
