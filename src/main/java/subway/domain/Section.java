package subway.domain;

import subway.domain.vo.SectionsRegister;

import java.util.Objects;

public class Section {

    private Long id;
    private Station upStation;
    private Station downStation;
    private Line line;
    private Distance distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, Line line, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = new Distance(distance);
    }

    public Section(Long id, Station upStation, Station downStation, Line line, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = new Distance(distance);
    }

    public boolean upStationEquals(Station station) {
        return upStation.equals(station);
    }

    public boolean downStationEquals(Station station) {
        return downStation.equals(station);
    }

    public boolean isOverDistance(Distance distance) {
        return this.distance.isOverDistance(distance);
    }

    public Section makeNewUpSection(Section duplicatedSection) {
        validateDistance(duplicatedSection);

        return new Section(
                duplicatedSection.id,
                this.downStation,
                duplicatedSection.downStation,
                this.line,
                duplicatedSection.distance.subtract(this.distance).getDistance()
        );
    }

    public Section makeNewDownSection(Section duplicatedDownSection) {
        validateDistance(duplicatedDownSection);

        return new Section(
                duplicatedDownSection.id,
                duplicatedDownSection.upStation,
                this.upStation,
                this.line,
                duplicatedDownSection.distance.subtract(this.distance).getDistance()
        );
    }

    private void validateDistance(Section duplicatedUpSection) {
        if (duplicatedUpSection.isOverDistance(this.distance)) {
            throw new IllegalArgumentException("기존 구간에 비해 거리가 길어 추가가 불가능 합니다.");
        }
    }

    public Section combineSection(Section otherSection) {
        return new Section(this.upStation, otherSection.downStation, this.line, this.distance.add(otherSection.distance).getDistance());
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public Distance getDistance() {
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
        return Objects.equals(id, section.id)
            && Objects.equals(upStation, section.upStation)
            && Objects.equals(downStation, section.downStation)
            && Objects.equals(line, section.line)
            && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, line, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", upStation=" + upStation +
            ", downStation=" + downStation +
            ", line=" + line +
            ", distance=" + distance +
            '}';
    }
}
