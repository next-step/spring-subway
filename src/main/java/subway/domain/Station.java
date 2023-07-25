package subway.domain;

import static subway.exception.ErrorCode.INVALID_STATION_NAME_BLANK;

import java.util.Objects;
import subway.exception.SubwayException;

public class Station {

    private final Long id;
    private final String name;

    public Station(final Long id, final String name) {
        validate(name);
        this.id = id;
        this.name = name;
    }

    public Station(final String name) {
        this(null, name);
    }

    private static void validate(final String name) {
        if (name == null || name.isBlank()) {
            throw new SubwayException(INVALID_STATION_NAME_BLANK);
        }
    }

    public boolean match(final Station other) {
        return this.equals(other);
    }

    public boolean isNotEqual(final Station other) {
        return !match(other);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(id, station.id) && Objects.equals(name, station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
