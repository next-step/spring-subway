package subway.domain;

import java.util.List;
import java.util.Objects;
import subway.application.dto.SectionParam;
import subway.exception.IllegalLineException;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;

public class Sections {

    private final ConnectedSection connectedSection;

    public Sections(final List<Section> sections) {
        validateLine(sections);
        connectedSection = new ConnectedSection(sections);
    }

    public Section updateOverlappedSection(final SectionParam params) {
        validateOnlyOneStationExistInLine(params);

        if (connectedSection.isDownDirectionSectionExist(params.getUpStation().getId())) {
            Section overlappedSection = connectedSection.getDownDirectionSection(
                params.getUpStation().getId());
            return updateOverlappedDownDirectionSection(overlappedSection, params);
        }

        if (connectedSection.isUpDirectionSectionExist(params.getDownStation().getId())) {
            Section overlappedSection = connectedSection.getUpDirectionSection(
                params.getDownStation().getId());
            return updateOverlappedUpDirectionSection(overlappedSection, params);
        }

        throw new IllegalSectionException("연결된 구간이 존재하지 않습니다.");
    }

    public boolean isLastStation(final long stationId) {
        return connectedSection.isLastStation(stationId);
    }

    public boolean isOverlapped(final SectionParam params) {
        validateOnlyOneStationExistInLine(params);

        boolean isUpStationOverlapped =
            connectedSection.isStationExist(params.getUpStation().getId()) &&
                !connectedSection.isEndStation(params.getUpStation().getId());
        boolean isDownStationOverlapped =
            connectedSection.isStationExist(params.getDownStation().getId()) &&
                !connectedSection.isStartStation(params.getDownStation().getId());

        return isUpStationOverlapped || isDownStationOverlapped;
    }

    public Section findUpDirectionSection(long stationId) {
        return connectedSection.getUpDirectionSection(stationId);
    }

    public Section findDownDirectionSection(long stationId) {
        return connectedSection.getDownDirectionSection(stationId);
    }

    public Section getLastSection(long stationId) {
        if (connectedSection.isLastStation(stationId)) {
            return connectedSection.getConnectedSection(stationId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalSectionException("연결된 구간이 존재하지 않습니다."));
        }

        throw new IllegalStationsException("종점 구간이 포함된 역이 아닙니다.");
    }

    public Line getLine() {
        return connectedSection.getAllSections()
            .stream()
            .map(Section::getLine)
            .findFirst()
            .orElseThrow(() -> new IllegalSectionException("구간이 소속된 노선이 존재하지 않습니다."));
    }

    private Section updateOverlappedDownDirectionSection(Section overlapped, SectionParam params) {
        validateDistance(overlapped, params.getDistance());
        return overlapped.narrowToDownDirection(overlapped.getUpStation(), params.getDistance());
    }


    private Section updateOverlappedUpDirectionSection(Section overlapped, SectionParam params) {
        validateDistance(overlapped, params.getDistance());
        return overlapped.narrowToUpDirection(overlapped.getDownStation(), params.getDistance());
    }

    private void validateOnlyOneStationExistInLine(final SectionParam params) {
        boolean upStationExist = connectedSection.isStationExist(params.getUpStation().getId());
        boolean downStationExist = connectedSection.isStationExist(params.getDownStation().getId());
        if (upStationExist == downStationExist) {
            throw new IllegalSectionException("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
        }
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
        return Objects.equals(connectedSection, sections.connectedSection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectedSection);
    }

    @Override
    public String toString() {
        return "Sections{" +
            "connectedSection=" + connectedSection +
            '}';
    }
}
