package subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubwayGraph {
    private Map<Station, List<Section>> connection = new HashMap<>();

    public SubwayGraph() {}

    public void add(Section section) {
        connection.computeIfAbsent(section.getStation(), (unused) -> new ArrayList<>()).add(section);
    }

    public List<Section> getSections(Station station) {
        System.out.println(connection);
        return connection.get(station);
    }
}
