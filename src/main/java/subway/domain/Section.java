package subway.domain;

import java.util.Objects;
import subway.domain.vo.SectionRegistVo;

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

    public boolean upStationEquals(Station station) {
        return upStation.equals(station);
    }

    public boolean downStationEquals(Station station) {
        return downStation.equals(station);
    }

    public boolean isOverDistance(Distance distance) {
        return (this.distance.compareDistance(distance) <= POSSIBLE_DISTANCE);
    }

    public SectionRegistVo registUpSection(Sections sections) {
        if (!sections.findUpStations().contains(upStation)) {
            return new SectionRegistVo(this);
        }
        return registMiddleUpSection(sections);
    }

    private SectionRegistVo registMiddleUpSection(Sections sections) {
        Section upSection = sections.findSectionByUpStation(upStation);

        validateDistance(upSection);

        Section modifiedSection = new Section(
            upSection.getId(),
            downStation,
            upSection.getDownStation(),
            line,
            upSection.getDistance().subtract(distance)
        );

        return new SectionRegistVo(this, modifiedSection);
    }

    public SectionRegistVo registDownSection(Sections sections) {
        if (!sections.findDownStations().contains(downStation)) {
            return new SectionRegistVo(this);
        }
        return registMiddleDownSection(sections);
    }

    private SectionRegistVo registMiddleDownSection(Sections sections) {
        Section downSection = sections.findSectionByDownStation(downStation);

        validateDistance(downSection);

        Section modifiedSection = new Section(
            downSection.getId(),
            downSection.getUpStation(),
            upStation,
            line,
            downSection.getDistance().subtract(distance)
        );

        return new SectionRegistVo(this, modifiedSection);
    }

    private void validateDistance(Section targetSection) {
        if (targetSection.isOverDistance(distance)) {
            throw new IllegalArgumentException("기존 구간에 비해 거리가 길어 추가가 불가능 합니다.");
        }
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
