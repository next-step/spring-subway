package subway.domain;

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
}
