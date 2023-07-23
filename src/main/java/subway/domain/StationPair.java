package subway.domain;

import java.util.Objects;

public class StationPair {

    private final Station upStation;
    private final Station downStation;

    public StationPair(final Station upStation, final Station downStation) {
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
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