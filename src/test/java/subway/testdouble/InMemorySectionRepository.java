package subway.testdouble;

import subway.domain.entity.Section;
import subway.domain.entity.Station;
import subway.domain.repository.SectionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemorySectionRepository implements SectionRepository {
    private final Map<Long, Section> sectionMap = new HashMap<>();

    @Override
    public Section insert(Section section) {
        sectionMap.put(section.getId(), section);
        return section;
    }

    @Override
    public List<Section> findAllByLineId(Long lineId) {
        return sectionMap.values().stream()
                .filter(section -> section.getLineId().equals(lineId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByStationId(Long lineId, Long stationId) {
        sectionMap.values()
                .removeIf(section -> section.getLineId().equals(lineId) && section.getDownStationId().equals(stationId));
    }
}
