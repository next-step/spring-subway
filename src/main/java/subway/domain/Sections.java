package subway.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import subway.application.dto.SectionParam;
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
        // 새로 추가할 구간 정보
        validateSection(params);

        if (upStationMap.containsKey(params.getUpStationId())) {
            // 새로 추가하는 구간의 상행역이 이미 존재함
            Section overlappedSection = upStationMap.get(params.getUpStationId());
            // 중복 상행 - (새로운 하행) - 기존 하행
            // overlappedSection 의 상행역을 params 의 하행역으로 narrow (downDirection)
            return updateOverlappedDownDirectionSection(overlappedSection, params);

        }

        // 새로 추가하는 구간의 하행역이 이미 존재함
        if (downStationMap.containsKey(params.getDownStationId())) {
            Section overlappedSection = downStationMap.get(params.getDownStationId());
            return updateOverlappedUpDirectionSection(overlappedSection, params);
        }

        throw new IllegalSectionException("연결된 구간이 존재하지 않습니다.");
    }

    public boolean isLastStation(final long stationId) {
        return isStartStation(stationId) || isEndStation(stationId);
    }

    public boolean isOverlapped(final SectionParam params) {
        // 두 역중 하나만 존재하고 있음을 검증 완료.
        validateSection(params);
        // params 의 up 이 기존에 존재할 경우, 하행종점역이면 안됨
        if (isStationExist(params.getUpStationId()) && isEndStation(params.getUpStationId())) {
            return false;
        }

        // params 의 down 이 기존에 존재할 경우, 상행종점역이면 안됨
        if (isStationExist(params.getDownStationId()) && isStartStation(params.getDownStationId())) {
            return false;
        }

        return true;
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

    // 중복 상행 - (새로운 하행) - 기존 하행
    // overlappedSection 의 상행역을 params 의 하행역으로 narrow (downDirection)
    private Section updateOverlappedDownDirectionSection(Section overlapped, SectionParam params) {
        final Station newUpStation = new Station();
        validateDistance(overlapped, params.getDistance());
        return overlapped.narrowToDownDirection(newUpStation, params.getDistance());
    }


    private Section updateOverlappedUpDirectionSection(Section overlapped, SectionParam params) {
        final Station newDownStation = new Station();
        validateDistance(overlapped, params.getDistance());
        return overlapped.narrowToUpDirection(newDownStation, params.getDistance());
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

    private void validateDistance(final Section existSection, final int distance) {
        if (existSection.isDistanceLessThanOrEqualTo(distance)) {
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
