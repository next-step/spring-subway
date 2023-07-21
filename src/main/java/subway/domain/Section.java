package subway.domain;

import org.springframework.util.Assert;

import java.util.Objects;

public class Section {

    private Long id;
    private Line line;
    private Station upStation;
    private Station downStation;
    private Distance distance;

    public Section(Line line, Station upStation, Station downStation, Distance distance) {
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

    public Section(Long id, Line line, Station upStation, Station downStation, Distance distance) {
        this(line, upStation, downStation, distance);
        this.id = id;
    }

    public Section() {
    }

    public Section cuttedSection(Section section) {
        validateDistance(section);

        if (this.upStation.equals(section.upStation)) {
            return new Section(section.line,
                    section.downStation,
                    this.downStation,
                    new Distance(this.getDistance() - section.getDistance()));
        }

        if (this.downStation.equals(section.downStation)) {
            return new Section(section.line,
                    this.upStation,
                    section.upStation,
                    new Distance(this.getDistance() - section.getDistance()));
        }

        throw new IllegalArgumentException("상행역과 하행역 중 하나는 같아야 합니다.");
    }

    private void validateDistance(Section section) {
        if (this.getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException("역사이에 역 등록시 구간이 기존 구간보다 작아야합니다.");
        }
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

    public Long getDistance() {
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
