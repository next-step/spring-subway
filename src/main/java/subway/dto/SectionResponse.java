package subway.dto;

import subway.domain.Section;

public class SectionResponse {

    private final long id;
    private final long lineId;
    private final long downStationId;
    private final long upStationId;
    private final int distance;

    public SectionResponse(long id, long lineId, long downStationId, long upStationId,
        int distance) {
        this.id = id;
        this.lineId = lineId;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    public static SectionResponse from(Section section) {
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
