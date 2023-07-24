package subway.domain;

import java.util.Objects;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

public class Station {

    private final String name;
    private Long id;

    public Station(final String name) {
        hasText(name, "이름은 필수입니다.");
        this.name = name;
    }

    public Station(final Long id, final String name) {
        this(name);
        notNull(id, "id는 필수입니다.");
        this.id = id;
    }

    public Long getId() {
        return id;
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
        return id.equals(station.id) && name.equals(station.name);
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
