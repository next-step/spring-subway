package subway.domain;

import java.util.Objects;

public class Station {
    private final Long id;
    private final StationName name;

    public Station(final Long id, final String name) {
        this(id, new StationName(name));
    }

    public Station(final Long id, final StationName name) {
        this.id = id;
        this.name = name;
    }

    public Station(final String name) {
        this(null, new StationName(name));
    }

    public Station(final StationName name) {
        this(null, name);
    }

    public Long getId() {
        return id;
    }

    public StationName getName() {
        return this.name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
