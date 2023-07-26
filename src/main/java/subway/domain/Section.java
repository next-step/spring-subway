package subway.domain;

import java.util.Objects;

public class Section {

    public static final int POSSIBLE_DISTANCE = 0;
    private Long id;
    private Station upStation;
    private Station downStation;
    private Line line;
    private Distance distance;

    public Section(Station upStation, Station downStation, Line line, int distance) {
        this(null, upStation, downStation, line, distance);
    }

    public Section(Long id, Station upStation, Station downStation, Line line, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = new Distance(distance);
    }

    private boolean isOverDistance(Distance distance) {
        return (this.distance.compareDistance(distance) <= POSSIBLE_DISTANCE);
    }

    public Section divideDownSection(Section targetSection) {
        validateDistance(targetSection);

        return new Section(
            targetSection.getId(),
            downStation,
            targetSection.getDownStation(),
            line,
            targetSection.getDistance().subtract(distance)
        );
    }

    public Section divideUpSection(Section targetSection) {
        validateDistance(targetSection);

        return new Section(
            targetSection.getId(),
            targetSection.getUpStation(),
            upStation,
            line,
            targetSection.getDistance().subtract(distance)
        );
    }

    private void validateDistance(Section targetSection) {
        if (targetSection.isOverDistance(distance)) {
            throw new IllegalArgumentException("기존 구간에 비해 거리가 길어 추가가 불가능 합니다.");
        }
    }

    public Section findDuplicatedSection(Section section) {
        if (upStation.equals(section.getUpStation())
            || downStation.equals(section.getDownStation())) {
            return section;
        }
        return null;
    }

    public Section linkToDown(Section section) {
        return new Section(
            upStation,
            section.getDownStation(),
            line,
            distance.getDistance() + section.getDistance().getDistance()
        );
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
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
