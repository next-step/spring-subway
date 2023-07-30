package subway.domain;

import java.util.List;
import subway.application.DefaultShortestPathFinder;

public class ShortestPath {

    private final List<Station> stations;
    private final int distance;

    public ShortestPath(final ShortestPathFinder pathFinder, final List<Section> sections,
        final Station source, final Station target) {
        validateNotEqual(source, target);
        pathFinder.calculatePath(sections, source, target);

        this.stations = pathFinder.getStations();
        this.distance = pathFinder.getDistance();
    }

    public static ShortestPath createDefault(final List<Section> sections, final Station source,
        final Station target) {
        return new ShortestPath(new DefaultShortestPathFinder(), sections, source, target);
    }

    private void validateNotEqual(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역은 같을 수 없습니다.");
        }
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
