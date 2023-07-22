package subway.dto;

import java.util.Objects;

public class StationUpdateRequest {
    private String name;

    public StationUpdateRequest() {
    }

    public StationUpdateRequest(String name) {
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
        if (!(o instanceof StationUpdateRequest)) {
            return false;
        }
        StationUpdateRequest that = (StationUpdateRequest) o;
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
