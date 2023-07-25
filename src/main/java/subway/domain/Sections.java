package subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import subway.exception.IllegalLineException;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;

public class Sections {

    private final Map<Long, Section> upStationMap = new HashMap<>();
    private final Map<Long, Section> downStationMap = new HashMap<>();

    public Sections(final List<Section> sections) {
        validateLine(sections);
        sections.forEach(section -> {
            upStationMap.put(section.getUpStationId(), section);
            downStationMap.put(section.getDownStationId(), section);
        });
    }

    public Optional<Section> findConnectedSection(final Section base) {
        validateStations(base.getUpStationId(), base.getDownStationId());

        if (upStationMap.containsKey(base.getUpStationId())) {
            return findConnectedUpStation(base);
        }

        if (downStationMap.containsKey(base.getDownStationId())) {
            return findConnectedDownStation(base);
        }

        return Optional.empty();
    }

    public boolean isLastStation(final long stationId) {
        return isStartStation(stationId) || isEndStation(stationId);
    }

    private boolean isStartStation(final long stationId) {
        return upStationMap.containsKey(stationId) && !downStationMap.containsKey(stationId);
    }

    private boolean isEndStation(final long stationId) {
        return !upStationMap.containsKey(stationId) && downStationMap.containsKey(stationId);
    }

    private Optional<Section> findConnectedUpStation(Section base) {
        Section conectedSection = upStationMap.get(base.getUpStationId());
        validateDistance(conectedSection, base);
        return Optional.of(conectedSection.upStationId(base));
    }

    private Optional<Section> findConnectedDownStation(Section base) {
        Section connectedSection = downStationMap.get(base.getDownStationId());
        validateDistance(connectedSection, base);
        return Optional.of(connectedSection.downStationId(base));
    }

    private void validateStations(long upStationId, long downStationId) {
        boolean upStationExist = isStationExist(upStationId);
        boolean downStationExist = isStationExist(downStationId);
        if (upStationExist == downStationExist) {
            throw new IllegalSectionException("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
        }
    }

    private boolean isStationExist(long stationId) {
        return upStationMap.containsKey(stationId) || downStationMap.containsKey(stationId);
    }

    private void validateLine(final List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalLineException("해당 노선은 생성되지 않았습니다.");
        }
    }

    private void validateDistance(final Section existSection, final Section other) {
        if (existSection.isDistanceLessThanOrEqualTo(other)) {
            throw new IllegalSectionException("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
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
        Sections sections = (Sections) o;
        return Objects.equals(upStationMap, sections.upStationMap)
            && Objects.equals(downStationMap, sections.downStationMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStationMap, downStationMap);
    }

    @Override
    public String toString() {
        return "Sections{" +
            "upStationMap=" + upStationMap +
            ", downStationMap=" + downStationMap +
            '}';
    }

    public Section findLeftSection(long stationId) {
        return downStationMap.get(stationId);
    }

    public Section findRightStation(long stationId) {
        return upStationMap.get(stationId);
    }

    public Section getLastSection(long stationId) {
        if (isStartStation(stationId)) {
            return upStationMap.get(stationId);
        }

        if (isEndStation(stationId)) {
            return downStationMap.get(stationId);
        }

        throw new IllegalStationsException("종점 구간이 포함된 역이 아닙니다.");
    }
}
