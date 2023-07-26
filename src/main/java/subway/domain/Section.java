package subway.domain;

import java.util.Objects;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

public class Section {

    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final Distance distance;
    private Long id;

    public Section(final Line line,
                   final Station upStation,
                   final Station downStation,
                   final Distance distance) {
        notNull(line, "노선은 null일 수 없습니다.");
        notNull(upStation, "상행역 null일 수 없습니다.");
        notNull(downStation, "하행역 null일 수 없습니다.");
        notNull(distance, "거리는 null일 수 없습니다.");
        isTrue(!upStation.getId().equals(downStation.getId()), "상행역과 하행역은 같을 수 없습니다.");
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(final Long id,
                   final Line line,
                   final Station upStation,
                   final Station downStation,
                   final Distance distance) {
        this(line, upStation, downStation, distance);
        notNull(id, "id는 필수입니다.");
        this.id = id;
    }

    public Distance addDistance(Section section) {
        return new Distance(this.getDistance() + section.getDistance());
    }

    public Distance subtractDistance(Section section) {
        if (getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException("기존 구간 길이보다 새로운 구간 길이가 같거나 더 클수는 없습니다.");
        }
        return new Distance(this.getDistance() - section.getDistance());
    }

    public boolean isNew() {
        return id == null;
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Long getLineId() {
        return line.getId();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Long getUpStationId() {
        return upStation.getId();
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDownStationId() {
        return downStation.getId();
    }

    public long getDistance() {
        return distance.getValue();
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
        return Objects.equals(id, section.id) && Objects.equals(line,
                section.line) && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation)
                && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
