package subway.domain;

import subway.exception.IllegalLineException;
import subway.exception.IllegalSectionException;

import java.util.*;

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

    private Optional<Section> findConnectedUpStation(Section base) {
        Section conectedSection = upStationMap.get(base.getUpStationId());
        validateDistance(conectedSection, base.getDistance());
        return Optional.of(conectedSection.upStationId(base));
    }

    private Optional<Section> findConnectedDownStation(Section base) {
        Section connectedSection = downStationMap.get(base.getDownStationId());
        validateDistance(connectedSection, base.getDistance());
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

    private void validateDistance(final Section existSection, final int distance) {
        if (!existSection.isDistanceGreaterThan(distance)) {
            throw new IllegalSectionException("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
        }
    }
}
