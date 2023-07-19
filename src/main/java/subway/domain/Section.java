package subway.domain;

import java.util.Objects;

public class Section {

    private static final String SAME_STATION_EXCEPTION_MESSAGE = "상행역과 하행역은 다른 역이어야 합니다.";

    private Long id;
    private Station upStation;
    private Station downStation;
    private Integer distance;

    public Section() {
    }

    public Section(Station upStation, Station downStation, Integer distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Long id, Station upStation, Station downStation, Integer distance) {
        validateDifferent(upStation, downStation);

        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validateDifferent(Station upStation, Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalArgumentException(SAME_STATION_EXCEPTION_MESSAGE);
        }
    }

    public boolean isUpConnected(Section other) {
        return upStation.equals(other.downStation);
    }

    public boolean isDownConnected(Section other) {
        return downStation.equals(other.upStation);
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

    public Integer getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }
}
