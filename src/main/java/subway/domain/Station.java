package subway.domain;

import java.util.Objects;

public class Station {
    private long id;
    private String name;

    public Station() {
    }

    public Station(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public boolean matchId(final long other) {
        return id == other;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return id == station.id && Objects.equals(name, station.name);
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
