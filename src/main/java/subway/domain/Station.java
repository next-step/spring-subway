package subway.domain;

import org.springframework.util.Assert;

import java.util.Objects;

public class Station {

    private Long id;
    private String name;

    public Station(final Long id, final String name) {
        Assert.hasText(name, "이름은 필수입니다.");
        Assert.notNull(id, "id는 null 일 수 없습니다");
        this.id = id;
        this.name = name;
    }

    public Station(final String name) {
        this.name = name;
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
