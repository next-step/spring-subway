package subway;

import subway.domain.Line;
import subway.domain.Station;

public class DomainFixture {

    public static final class Section {

        public static subway.domain.Section buildWithStations(Line line, Station upStation, Station downStation) {
            return buildWithStations(1L, line, upStation, downStation, 10);
        }

        public static subway.domain.Section buildWithStations(Station upStation, Station downStation,
                Integer distance) {
            return buildWithStations(1L, null, upStation, downStation, distance);
        }

        public static subway.domain.Section buildWithStations(Station upStation, Station downStation) {
            return buildWithStations(1L, null, upStation, downStation, 10);
        }

        public static subway.domain.Section buildWithStations(Long id, Line line, Station upStation,
                Station downStation, Integer distance) {
            return subway.domain.Section.builder()
                    .id(id)
                    .line(line)
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build();
        }
    }

}
