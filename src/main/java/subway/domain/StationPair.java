package subway.domain;

import java.util.Objects;
import subway.exception.IllegalStationsException;

public class StationPair {

    private final Station upStation;
    private final Station downStation;

    public StationPair(final Station upStation, final Station downStation) {
        validate(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public boolean matchUpStation(Station other) {
        return this.upStation.equals(other);
    }

    public boolean matchDownStation(Station other) {
        return this.downStation.equals(other);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    private void validate(final Station upStation, final Station downStation) {
        if (upStation.equals(downStation)) {
            throw new IllegalStationsException("상행역과 하행역은 동일할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StationPair that = (StationPair) o;
        return Objects.equals(upStation, that.upStation) && Objects.equals(
            downStation, that.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation);
    }

    @Override
    public String
    toString() {
        return "StationPair{" +
            "upStation=" + upStation +
            ", downStation=" + downStation +
            '}';
    }
}
