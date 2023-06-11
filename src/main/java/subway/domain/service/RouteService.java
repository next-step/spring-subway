package subway.domain.service;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import subway.domain.entity.Section;
import subway.domain.entity.Station;
import subway.domain.repository.SectionRepository;
import subway.domain.repository.StationRepository;
import subway.domain.vo.Route;
import subway.util.FareCalculator;

import java.util.List;

@Service
public class RouteService {
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public RouteService(StationRepository stationRepository, SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public Route getShortestRoute(String source, String destination) {
        Station sourceStation = stationRepository.findByName(source);
        Station destinationStation = stationRepository.findByName(destination);
        DijkstraShortestPath<Station, Section> dijkstraShortestPath = getShortestPath();
        GraphPath<Station, Section> path = dijkstraShortestPath.getPath(sourceStation, destinationStation);
        int distance = (int) Math.round(path.getWeight());
        return new Route(path.getVertexList(), distance, FareCalculator.calculateByDistance(distance));
    }

    private DijkstraShortestPath<Station, Section> getShortestPath() {
        List<Station> allStations = stationRepository.findAll();
        List<Section> allSections = sectionRepository.findAll();
        WeightedMultigraph<Station, Section> graph = new WeightedMultigraph<>(Section.class);

        for (Station station : allStations) {
            graph.addVertex(station);
        }
        for (Section section : allSections) {
            Station upStation = allStations.stream()
                    .filter(station -> station.getId().equals(section.getUpStationId()))
                    .findFirst().get();
            Station downStation = allStations.stream()
                    .filter(station -> station.getId().equals(section.getDownStationId()))
                    .findFirst().get();
            graph.setEdgeWeight(graph.addEdge(upStation, downStation), section.getDistance());
        }
        return new DijkstraShortestPath<>(graph);
    }
}
