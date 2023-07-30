package subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import subway.dto.ShortestSubwayPath;

public class Subway {
    private final List<LineSections> lineSections;

    public Subway(List<Section> allSections) {
        this.lineSections = mapToLineSections(allSections);
    }

    private List<LineSections> mapToLineSections(List<Section> values) {
        Map<Line, List<Section>> lineSectionsMap = new HashMap<>();
        values.forEach(section -> lineSectionsMap.computeIfAbsent(section.getLine(), k -> new ArrayList<>()).add(section));

        return lineSectionsMap.entrySet().stream()
            .map(entry -> new LineSections(entry.getKey(), new Sections(entry.getValue())))
            .collect(Collectors.toList());
    }

    public ShortestSubwayPath calculateShortestPath(Station sourceStation, Station destinationStation) {
        SubwayPath subwayPath = new SubwayPath(this.lineSections);
        return subwayPath.calculateShortestPath(sourceStation, destinationStation);
    }

    public List<LineSections> getLineSections() {
        return Collections.unmodifiableList(lineSections);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Subway)) {
            return false;
        }
        Subway subway = (Subway) o;
        return Objects.equals(lineSections, subway.lineSections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineSections);
    }
}
