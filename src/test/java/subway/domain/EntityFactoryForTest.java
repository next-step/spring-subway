package subway.domain;

public class EntityFactoryForTest {
    public static Section makeSection(long lineId, long upStationId, long downStationId, Integer distance) {
        Line line = new Line(lineId, "name");
        Station upStation = new Station(upStationId);
        Station downStation = new Station(downStationId);

        return Section.of(line, upStation, downStation, distance);
    }
}
