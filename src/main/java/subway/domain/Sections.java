package subway.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import subway.exception.IllegalStationsException;

public class Sections {

    private final Map<Long, Section> upStationMap = new HashMap<>();
    private final Map<Long, Section> downStationMap = new HashMap<>();

    public Sections(final List<Section> sections) {
        sections.forEach(section -> {
            upStationMap.put(section.getUpStation().getId(), section);
            downStationMap.put(section.getDownStation().getId(), section);
        });
    }

    public List<Section> getConnectedSection(long stationId) {
        List<Section> sections = new ArrayList<>();

        if (isDownDirectionSectionExist(stationId)) {
            sections.add(upStationMap.get(stationId));
        }

        if (isUpDirectionSectionExist(stationId)) {
            sections.add(downStationMap.get(stationId));
        }

        return sections;
    }

    public Section getUpDirectionSection(long stationId) {
        return downStationMap.get(stationId);
    }

    public Section getDownDirectionSection(long stationId) {
        return upStationMap.get(stationId);
    }

    public List<Section> getAll() {
        Stream<Section> downDirectionSections = upStationMap.keySet()
            .stream()
            .map(upStationMap::get);
        Stream<Section> upDirectionSections = downStationMap.keySet()
            .stream()
            .map(downStationMap::get);

        return Stream.concat(downDirectionSections, upDirectionSections)
            .collect(Collectors.toList());
    }

    public Station getStationById(long stationId) {
        if (isDownDirectionSectionExist(stationId)) {
            return upStationMap.get(stationId).getUpStation();
        }

        if (isUpDirectionSectionExist(stationId)) {
            return downStationMap.get(stationId).getDownStation();
        }

        throw new IllegalStationsException("존재하지 않는 역 정보입니다.");
    }

    public List<Station> getAllStations() {
        return upStationMap.keySet().stream()
            .flatMap(key -> Stream.of(upStationMap.get(key).getUpStation(), upStationMap.get(key).getDownStation()))
            .distinct()
            .collect(Collectors.toList());
    }

    public boolean isDownDirectionSectionExist(final long stationId) {
        return upStationMap.containsKey(stationId);
    }

    public boolean isUpDirectionSectionExist(final long stationId) {
        return downStationMap.containsKey(stationId);
    }

    public boolean isLastStation(final long stationId) {
        return isStartStation(stationId) || isEndStation(stationId);
    }

    public boolean isStationExist(long stationId) {
        return isDownDirectionSectionExist(stationId)|| isUpDirectionSectionExist(stationId);
    }

    public boolean isStartStation(final long stationId) {
        return upStationMap.containsKey(stationId) && !downStationMap.containsKey(stationId);
    }

    public boolean isEndStation(final long stationId) {
        return !upStationMap.containsKey(stationId) && downStationMap.containsKey(stationId);
    }
}
