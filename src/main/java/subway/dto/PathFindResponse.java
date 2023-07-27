package subway.dto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PathFindResponse {

    private final List<PathFindResponse.StationResponse> stations;
    private final int distance;

    public PathFindResponse(List<PathFindResponse.StationResponse> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<PathFindResponse.StationResponse> getStations() {
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
        if (!(o instanceof PathFindResponse)) {
            return false;
        }
        PathFindResponse that = (PathFindResponse) o;
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
            if (!(o instanceof PathFindResponse.StationResponse)) {
                return false;
            }
            PathFindResponse.StationResponse that = (PathFindResponse.StationResponse) o;
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