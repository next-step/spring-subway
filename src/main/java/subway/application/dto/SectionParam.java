package subway.application.dto;

import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

public class SectionParam {

    private final long lineId;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public SectionParam(long lineId, Station upStationId, Station downStation, int distance) {
        this.lineId = lineId;
        this.upStation = upStationId;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section to(Line line) {
        return new Section(line, upStation, downStation, distance);
    }

    public long getLineId() {
        return lineId;
    }

    public long getUpStationId() {
        return upStation.getId();
    }

    public long getDownStationId() {
        return downStation.getId();
    }

    public int getDistance() {
        return distance;
    }
}
