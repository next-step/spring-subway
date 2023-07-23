package subway.domain;

import static org.springframework.util.StringUtils.hasText;

import java.util.Objects;

public class Station {

    private Long id;
    private String name;

    public Station(Long id, String name) {
        validateStation(name);
        this.id = id;
        this.name = name;
    }

    public Station(String name) {
        this(null, name);
    }

    private void validateStation(String name) {
        if (!hasText(name)) {
            throw new IllegalArgumentException("이름을 반드시 입력해야합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Station{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
