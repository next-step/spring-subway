package subway.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import subway.domain.Station;

import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubwayPath {
    private final List<Station> stations;
    private final Double distance;

    public static SubwayPath of(List<Station> stations, Double distance) {
        return new SubwayPath(stations, distance);
    }
    public static SubwayPath of(List<Station> stations, Integer distance) {
        return new SubwayPath(stations, (double)distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubwayPath subwayPath = (SubwayPath) o;
        return Objects.equals(stations, subwayPath.stations) && Objects.equals(distance, subwayPath.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations, distance);
    }

    @Override
    public String toString() {
        return "Path{" +
                "stations=" + stations +
                ", distance=" + distance +
                '}';
    }
}
