package subway;

import subway.domain.Line;
import subway.domain.Station;

public class DomainFixture {

    public static final class Section {

        public static subway.domain.Section buildWithSectionAndStation(subway.domain.Section section,
                Station downStation) {
            return buildWithStations(section.getId(), section.getUpStation(), downStation,
                    section.getDistance());
        }

        public static subway.domain.Section buildWithStations(Station upStation, Station downStation,
                Integer distance) {
            return buildWithStations(null, upStation, downStation, distance);
        }

        public static subway.domain.Section buildWithStations(Station upStation, Station downStation) {
            return buildWithStations(null, upStation, downStation, 10);
        }

        public static subway.domain.Section buildWithStations(Long id, Station upStation,
                Station downStation, Integer distance) {
            return subway.domain.Section.builder()
                    .id(id)
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build();
        }
    }

}
