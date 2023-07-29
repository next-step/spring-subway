package subway.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WholeSection {
    private List<Section> sections;

    public WholeSection(final List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getAllStations() {
        Set<Station> stations = new HashSet<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    public List<Section> getAllSections() {
        return new ArrayList<>(sections);
    }
}
