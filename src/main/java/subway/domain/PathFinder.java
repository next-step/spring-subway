package subway.domain;

public interface PathFinder {

    Path findShortPath(Station source, Station target);

    int calculateCharge(int distance);
}
