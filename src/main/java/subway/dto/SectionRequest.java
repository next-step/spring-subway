package subway.dto;

import org.springframework.util.Assert;
import subway.domain.Section;

public class SectionRequest {


    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private SectionRequest() {
    }

    public SectionRequest(final String upStationId, final String downStationId, final Integer distance) {
        Assert.notNull(upStationId, "상행역을 입력해야 합니다.");
        Assert.notNull(downStationId, "하행역을 입력해야 합니다.");
        Assert.notNull(distance, "거리를 입력해야 합니다.");

        this.upStationId = Long.parseLong(upStationId);
        this.downStationId = Long.parseLong(downStationId);
        this.distance = distance;
    }

    public Section to(final Long lineId) {
        return new Section(lineId, upStationId, downStationId, distance);
    }
}
