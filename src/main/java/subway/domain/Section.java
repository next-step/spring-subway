package subway.domain;

import org.springframework.util.Assert;

import java.util.Objects;

public class Section {

    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private Distance distance;

    public Section(final Line line,
                   final Station upStation,
                   final Station downStation,
                   final Distance distance) {
        Assert.notNull(line, "노선은 null일 수 없습니다.");
        Assert.notNull(upStation, "상행역 null일 수 없습니다.");
        Assert.notNull(downStation, "하행역 null일 수 없습니다.");
        Assert.notNull(distance, "거리는 null일 수 없습니다.");
        Assert.isTrue(!upStation.getId().equals(downStation.getId()), "상행역과 하행역은 같을 수 없습니다.");
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
        this.id = id;
    }

    public Section(Section section) {
        this(section.id, section.line, section.upStation, section.downStation, section.distance);
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

    public boolean isNew() {
        return id == null;
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
