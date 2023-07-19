package subway.domain;

public class SectionStation {

    private Station station;
    private Station downStation;

    public SectionStation(final Station station, final Station downStation) {
        this.station = station;
        this.downStation = downStation;
    }

    public Station getStation() {
        return station;
    }

    public Station getDownStation() {
        return downStation;
    }
}
