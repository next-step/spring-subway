package subway.domain;

import java.util.Objects;

public class Station {
    private final Long id;
    private final Name name;

    public Station(Long id, String name) {
        this.id = id;
        this.name = new Name(name);
    }

    public Station(String name) {
        this.id = null;
        this.name = new Name(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Station)) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
