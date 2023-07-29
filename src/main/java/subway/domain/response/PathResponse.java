package subway.domain.response;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PathResponse {

    private final List<PathResponse.StationResponse> stations;
    private final int distance;

    public PathResponse(List<PathResponse.StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<PathResponse.StationResponse> getStations() {
        return Collections.unmodifiableList(stations);
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PathResponse)) {
            return false;
        }
        PathResponse that = (PathResponse) o;
        return distance == that.distance && Objects.equals(stations, that.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations, distance);
    }

    @Override
    public String toString() {
        return "PathResponse{" +
                "stations=" + stations +
                ", distance=" + distance +
                '}';
    }

    public static final class StationResponse {
        private final long id;
        private final String name;

        public StationResponse(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() {
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
            if (!(o instanceof PathResponse.StationResponse)) {
                return false;
            }
            PathResponse.StationResponse that = (PathResponse.StationResponse) o;
            return id == that.id && Objects.equals(name, that.name);
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
}