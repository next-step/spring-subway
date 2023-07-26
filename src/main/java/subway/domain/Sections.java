package subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import subway.dto.SectionParam;
import subway.exception.IllegalLineException;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;

public class Sections {

    private final Map<Long, Section> upStationMap = new HashMap<>();
    private final Map<Long, Section> downStationMap = new HashMap<>();

    public Sections(final List<Section> sections) {
        validateLine(sections);
        sections.forEach(section -> {
            upStationMap.put(section.getUpStation().getId(), section);
            downStationMap.put(section.getDownStation().getId(), section);
        });
    }

    public Section updateOverlappedSection(final SectionParam params) {
        validateSection(params);

        if (upStationMap.containsKey(params.getUpStationId())) {
            Section downDirectionSection = upStationMap.get(params.getUpStationId());
            return updateOverlappedDownDirectionSection(downDirectionSection);
        }

        if (downStationMap.containsKey(params.getDownStationId())) {
            Section upDirectionSection = downStationMap.get(params.getDownStationId());
            return updateOverlappedUpDirectionSection(upDirectionSection);
        }

        throw new IllegalSectionException("연결된 구간이 존재하지 않습니다.");
    }

    public boolean isLastStation(final long stationId) {
        return isStartStation(stationId) || isEndStation(stationId);
    }

    public boolean isOverlapped(final SectionParam params) {
        validateSection(params);
        return !isLastStation(params.getUpStationId()) && !isLastStation(
            params.getDownStationId());
    }

    public Section findUpDirectionSection(long stationId) {
        return downStationMap.get(stationId);
    }

    public Section findDownDirectionSection(long stationId) {
        return upStationMap.get(stationId);
    }

    public Section getLastSection(long stationId) {
        if (isStartStation(stationId)) {
            return findDownDirectionSection(stationId);
        }

        if (isEndStation(stationId)) {
            return findUpDirectionSection(stationId);
        }

        throw new IllegalStationsException("종점 구간이 포함된 역이 아닙니다.");
    }

    public Line getLine() {
        return upStationMap.values()
            .stream()
            .map(Section::getLine)
            .findFirst()
            .orElseThrow(() -> new IllegalSectionException("구간이 소속된 노선이 존재하지 않습니다."));
    }

    private boolean isStartStation(final long stationId) {
        return upStationMap.containsKey(stationId) && !downStationMap.containsKey(stationId);
    }

    private boolean isEndStation(final long stationId) {
        return !upStationMap.containsKey(stationId) && downStationMap.containsKey(stationId);
    }

    private Section updateOverlappedDownDirectionSection(Section base) {
        final Section downDirectionSection = findDownDirectionSection(base.getUpStation().getId());
        validateDistance(downDirectionSection, base);
        return downDirectionSection.narrowToUpDirection(base);
    }

    private Section updateOverlappedUpDirectionSection(Section base) {
        final Section upDirectionSection = findUpDirectionSection(base.getDownStation().getId());
        validateDistance(upDirectionSection, base);
        return upDirectionSection.narrowToDownDirection(base);
    }

    private void validateSection(final SectionParam params) {
        boolean upStationExist = isStationExist(params.getUpStationId());
        boolean downStationExist = isStationExist(params.getDownStationId());
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
}
