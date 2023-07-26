package subway.domain;

import java.util.Objects;

public class Station {

    private Long id;
    private Name name;

    public Station(final String name) {
        this(new Name(name));
    }

    public Station(final Name name) {
        this.name = name;
    }

    public Station(final Long id, final String name) {
        this(id, new Name(name));
    }

    public Station(final Long id, final Name name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public Name getName() {
        return name;
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
