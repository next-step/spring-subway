package subway.domain;

import subway.dto.StationResponse;

import java.util.*;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = sort(new ArrayList<>(sections));
    }

    private List<Section> sort(final List<Section> sections) {
        final Map<Long, Section> upStationIdToSection = sections.stream()
                .collect(Collectors.toMap(Section::getUpStationId, section -> section));

        // 상행 종점 구하기
        Set<Long> keySet = new HashSet<>(upStationIdToSection.keySet());
        Set<Long> valueSet = upStationIdToSection.values().stream()
                .map(Section::getDownStationId)
                .collect(Collectors.toSet());

        keySet.removeAll(valueSet);
        Long startStationId = keySet.stream().findAny().orElseThrow();

        List<Section> response = new ArrayList<>();

        Long stationId = startStationId;
        while(upStationIdToSection.containsKey(stationId)) {
            response.add(upStationIdToSection.get(stationId));
            stationId = upStationIdToSection.get(stationId).getDownStationId();
        }

        return response;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
