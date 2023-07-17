package subway.domain;

import java.util.Objects;
import org.springframework.util.Assert;

public class Section {

    private static final long MIN_DISTANCE = 1L;
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public Section(Long id, Long lineId, Long upStationId, Long downStationId, Long distance) {
        this(lineId, upStationId, downStationId, distance);
        this.id = id;
    }

    public Section(Long lineId, Long upStationId, Long downStationId, Long distance) {
        validate(lineId, upStationId, downStationId, distance);

        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    private void validate(Long lineId, Long upStationId, Long downStationId,
            Long distance) {
        Assert.notNull(lineId, "노선 아이디는 null일 수 없습니다.");
        Assert.notNull(upStationId, "상행역 아이디는 null일 수 없습니다.");
        Assert.notNull(downStationId, "하행역 아이디는 null일 수 없습니다.");
        Assert.notNull(distance, "거리는 null일 수 없습니다.");
        Assert.isTrue(!upStationId.equals(downStationId), "상행역 아이디와 하행역 아이디는 같을 수 없습니다.");
        Assert.isTrue(distance >= MIN_DISTANCE, "거리는 " + MIN_DISTANCE + "이상이어야 합니다.");
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(lineId,
                section.lineId) && Objects.equals(upStationId, section.upStationId)
                && Objects.equals(downStationId, section.downStationId)
                && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lineId, upStationId, downStationId, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", upStationId=" + upStationId +
                ", downStationId=" + downStationId +
                ", distance=" + distance +
                '}';
    }
}
