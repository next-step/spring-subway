package subway.domain;

import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;

import java.util.Objects;

public class Station {

    private static final int NAME_LENGTH_LIMIT = 255;

    private Long id;
    private String name;

    public Station() {
    }

    public Station(final Long id, final String name) {
        validateNotNull(name);
        validateLength(name);

        this.id = id;
        this.name = name;
    }

    private void validateLength(String name) {
        if (name.length() > NAME_LENGTH_LIMIT) {
            throw new IncorrectRequestException(ErrorCode.LONG_STATION_NAME, "입력값: " + name);
        }
    }

    private void validateNotNull(String name) {
        if (name == null) {
            throw new IncorrectRequestException(ErrorCode.NULL_STATION_NAME, "입력값: " + name);
        }
    }

    public Station(final String name) {
        this(null, name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id.equals(station.id) && name.equals(station.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
