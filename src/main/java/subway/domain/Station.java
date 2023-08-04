package subway.domain;

import java.util.Objects;

public class Station {

    private Long id;
    private final StationName stationName;

    public Station(final String stationName) {
        this(new StationName(stationName));
    }

    public Station(final StationName stationName) {
        this.stationName = stationName;
    }

    public Station(final Long id, final String stationName) {
        this(id, new StationName(stationName));
    }

    public Station(final Long id, final StationName stationName) {
        this(stationName);
        this.id = id;
    }

    public boolean hasSmallerIdThan(Station other) {
        return id < other.getId();
    }

    public Long getId() {
        return id;
    }

    public StationName getStationName() {
        return stationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id.equals(station.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
