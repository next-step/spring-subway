package subway.domain;

import java.util.Objects;

public class Section {
    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    public Section(final Long id, final Station upStation, final Station downStation, final int distance) {
        validate(upStation, downStation);

        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this(null, upStation, downStation, distance);
    }

    private void validate(final Station upStation, final Station downStation) {
        if (upStation == downStation) {
            throw new IllegalArgumentException("상행역과 하행역이 같을 수 없습니다");
        }
    }

    public Long getId() {
        return id;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }
}
