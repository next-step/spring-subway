package subway.domain;

public class DomainFixture {

    public static final class Section {

        public static subway.domain.Section buildWithSectionAndStation(subway.domain.Section section,
                Station downStation) {
            return buildWithStations(section.getId(), section.getUpStation(), downStation,
                    section.getDistance());
        }

        public static subway.domain.Section buildWithStations(Station upStation, Station downStation,
                int distance) {
            return buildWithStations(1L, upStation, downStation, distance);
        }

        public static subway.domain.Section buildWithStations(Station upStation, Station downStation) {
            return buildWithStations(1L, upStation, downStation, 10);
        }

        public static subway.domain.Section buildWithStations(Long id, Station upStation, Station downStation,
                int distance) {
            return subway.domain.Section.builder()
                    .id(id)
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build();
        }
    }

}
