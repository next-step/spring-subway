package subway;

import subway.domain.Station;

public class TestHelper {

    public static final class Section {

        public static subway.domain.Section buildWithStations(Station upStation, Station downStation) {
            return buildWithStations(upStation, downStation, 10);
        }

        public static subway.domain.Section buildWithStations(Station upStation, Station downStation,
                Integer distance) {
            return subway.domain.Section.builder()
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build();
        }
    }

}
