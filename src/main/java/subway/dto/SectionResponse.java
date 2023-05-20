package subway.dto;

import subway.domain.Section;

public class SectionResponse {

    private long id;
    private long lineId;
    private long downStationId;
    private long upStationId;
    private int distance;

    private SectionResponse(long id, long lineId, long downStationId, long upStationId, int distance) {
        this.id = id;
        this.lineId = lineId;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section.getId(), section.getLineId(), section.getDownStationId(),
                section.getUpStationId(), section.getDistance());
    }

    public long getId() {
        return id;
    }

    public long getLineId() {
        return lineId;
    }

    public long getDownStationId() {
        return downStationId;
    }

    public long getUpStationId() {
        return upStationId;
    }

    public int getDistance() {
        return distance;
    }
}
