package subway.service;

import org.junit.jupiter.api.Test;
import subway.domain.entity.Line;
import subway.domain.entity.Section;
import subway.domain.entity.Station;
import subway.domain.repository.LineRepository;
import subway.domain.repository.SectionRepository;
import subway.domain.repository.StationRepository;
import subway.domain.service.RouteService;
import subway.domain.vo.Route;
import subway.testdouble.InMemoryLineRepository;
import subway.testdouble.InMemorySectionRepository;
import subway.testdouble.InMemoryStationRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RouteServiceTest {
    private final LineRepository lineRepository = new InMemoryLineRepository();
    private final StationRepository stationRepository = new InMemoryStationRepository();
    private final SectionRepository sectionRepository = new InMemorySectionRepository();
    private final RouteService routeService = new RouteService(stationRepository, sectionRepository);

    @Test
    void getShortestRoute() {
        lineRepository.insert(new Line(1L, "2호선", "green"));
        lineRepository.insert(new Line(2L, "4호선", "blue"));
        stationRepository.insert(new Station(1L, "방배"));
        stationRepository.insert(new Station(2L, "사당"));
        stationRepository.insert(new Station(3L, "남태령"));
        sectionRepository.insert(new Section(1L, 2L, 1L, 1L, 10));
        sectionRepository.insert(new Section(2L, 3L, 2L, 1L, 3));

        Route shortestRoute = routeService.getShortestRoute("방배", "남태령");

        assertThat(shortestRoute.getStations().size()).isEqualTo(3);
        assertThat(shortestRoute.getDistance()).isEqualTo(13);
        assertThat(shortestRoute.getPrice()).isEqualTo(1350);
    }
}
