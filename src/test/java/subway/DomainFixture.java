package subway;

import subway.domain.Line;
import subway.domain.Station;

public class DomainFixture {

    public static final class Section {

        public static subway.domain.Section buildWithStations(Line line, Station upStation, Station downStation) {
            return buildWithStations(line, upStation, downStation, 10);
        }

        public static subway.domain.Section buildWithStations(Station upStation, Station downStation) {
            return buildWithStations(null, upStation, downStation, 10);
        }

        public static subway.domain.Section buildWithStations(Line line, Station upStation, Station downStation,
                Integer distance) {
            return subway.domain.Section.builder()
                    .line(line)
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build();
        }
    }

}
