package subway.domain;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class Sections {

    private static final int MINIMUM_SIZE = 1;
    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void validSectionsLine() {
        long sectionsLinesCount = sections.stream()
            .map(Section::getLine)
            .distinct()
            .count();

        if (sectionsLinesCount > 1L) {
            throw new IllegalArgumentException("서로 다른 호선의 구간이 들어가 있습니다.");
        }
    }

    public void validDeleteStation(Station deleteStation) {
        validSectionsCount();
        validStationInSections(deleteStation);
    }

    private void validSectionsCount() {
        if (sections.size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException(
                MessageFormat.format("구간이 {0}개 이하이므로 해당역을 삭제할 수 없습니다.", MINIMUM_SIZE)
            );
        }
    }

    private void validStationInSections(Station deleteStation) {
        if (!findStations().contains(deleteStation)) {
            throw new IllegalArgumentException("등록되지 않은 역을 제거할 수 없습니다.");
        }
    }

    public List<Station> sortStations() {
        List<Station> sortedStations = new ArrayList<>();
        Map<Station, Section> sectionByUpStation = initSectionByUpStation();

        Station nowStation = findTopStation();
        sortedStations.add(nowStation);

        while (sectionByUpStation.containsKey(nowStation)) {
            Section section = sectionByUpStation.get(nowStation);
            sortedStations.add(section.getDownStation());
            nowStation = section.getDownStation();
        }

        return Collections.unmodifiableList(sortedStations);
    }

    private Map<Station, Section> initSectionByUpStation() {
        return sections.stream()
            .collect(Collectors.toMap(
                Section::getUpStation,
                Function.identity()
            ));
    }

    private Station findTopStation() {
        return findStations().stream()
            .filter(station -> !findDownStations().contains(station))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("현재 노선의 역 정보가 올바르지 않습니다."));
    }

    public void validRegisterSection(Section section) {
        if (isSectionDuplicated(section)) {
            throw new IllegalArgumentException("기존 구간의 상행역과 하행역이 중복 됩니다.");
        }
        if (isStationNotInSections(section)) {
            throw new IllegalArgumentException("등록하고자 하는 구간의 2개의 역이 모두 노선에 포함되지 않아 추가할 수 없습니다.");
        }
    }

    private boolean isSectionDuplicated(Section section) {
        return findStations().contains(section.getUpStation()) && findStations().contains(
            section.getDownStation());
    }

    private boolean isStationNotInSections(Section section) {
        return !findStations().contains(section.getUpStation())
            && !findStations().contains(section.getDownStation())
            && !sections.isEmpty();
    }

    public Optional<Section> makeUpdateSection(Section registerSection) {
        if (findUpStations().contains(registerSection.getUpStation())) {
            return Optional.of(makeNewSectionByUpStation(registerSection));
        }
        if (findDownStations().contains(registerSection.getDownStation())) {
            return Optional.of(makeNewSectionByDownStation(registerSection));
        }
        return Optional.empty();
    }

    private Set<Station> findUpStations() {
        return sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toSet());
    }

    private Set<Station> findDownStations() {
        return sections.stream()
            .map(Section::getDownStation)
            .collect(Collectors.toSet());
    }

    private Section makeNewSectionByUpStation(Section registerSection) {
        Section duplicatedUpSection = findSectionByUpStation(registerSection.getUpStation())
            .orElseThrow(() -> new IllegalStateException("현재 구간에 등록된 정보가 올바르지 않습니다."));
        return registerSection.makeNewUpSection(duplicatedUpSection);
    }

    private Section makeNewSectionByDownStation(Section registerSection) {
        Section duplicatedDownSection = findSectionByDownStation(registerSection.getDownStation())
            .orElseThrow(() -> new IllegalStateException("현재 구간에 등록된 정보가 올바르지 않습니다."));
        return registerSection.makeNewDownSection(duplicatedDownSection);
    }

    public Optional<Section> findSectionByDownStation(Station station) {
        return sections.stream()
            .filter(section -> section.downStationEquals(station))
            .findFirst();
    }

    public Optional<Section> findSectionByUpStation(Station station) {
        return sections.stream()
            .filter(section -> section.upStationEquals(station))
            .findFirst();
    }

    private Set<Station> findStations() {
        return sections.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .collect(Collectors.toUnmodifiableSet());
    }

    public Distance findSourceToTargetDistance(Station sourceStation, Station targetStation) {
        DijkstraShortestPath dijkstraShortestPath = makeDijkstraShortestPath();

        double pathWeight = dijkstraShortestPath.getPathWeight(sourceStation, targetStation);

        validPathWeight(pathWeight);

        return new Distance((int) pathWeight);
    }

    private void validPathWeight(double pathWeight) {
        if (Double.isInfinite(pathWeight)) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어있지 않습니다.");
        }
        if (pathWeight == 0.0) {
            throw new IllegalArgumentException("출발역과 도착역이 동일합니다.");
        }
    }

    public List<Station> findSourceToTargetRoute(Station sourceStation, Station targetStation) {
        DijkstraShortestPath dijkstraShortestPath = makeDijkstraShortestPath();

        List<Station> shortestPath = makeShortestPath(
            sourceStation,
            targetStation,
            dijkstraShortestPath
        );

        validSameSourceAndTarget(shortestPath);

        return shortestPath;
    }

    private DijkstraShortestPath makeDijkstraShortestPath() {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph
            = new WeightedMultigraph(DefaultWeightedEdge.class);

        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance().getDistance()
            );
        }

        return new DijkstraShortestPath(graph);
    }

    private List<Station> makeShortestPath(
        Station sourceStation,
        Station targetStation,
        DijkstraShortestPath dijkstraShortestPath
    ) {
        try {
            return dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어있지 않습니다.");
        }
    }

    private void validSameSourceAndTarget(List<Station> shortestPath) {
        if (shortestPath.size() == 1) {
            throw new IllegalArgumentException("출발역과 도착역이 동일합니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }

    @Override
    public String toString() {
        return "Sections{" +
            "sections=" + sections +
            '}';
    }
}
