package subway.dto;

import java.util.Objects;

public class StationCreateRequest {
    private String name;

    public StationCreateRequest() {
    }

    public StationCreateRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StationCreateRequest)) {
            return false;
        }
        StationCreateRequest that = (StationCreateRequest) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "StationRequest{" +
                "name='" + name + '\'' +
                '}';
    }
}
