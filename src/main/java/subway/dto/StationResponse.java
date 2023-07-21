package subway.dto;

import java.util.Objects;
import subway.domain.Station;

public class StationResponse {
    private final String id;
    private final String name;

    public StationResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(String.valueOf(station.getId()), station.getName());
    }

    public String getId() {
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
        if (!(o instanceof StationResponse)) {
            return false;
        }
        StationResponse that = (StationResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "StationResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
