package subway.domain;

import java.util.List;
import java.util.Objects;
import subway.application.dto.SectionParam;
import subway.exception.IllegalLineException;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;

public class SectionUpdater {

    private final Sections sections;

    public SectionUpdater(final List<Section> sections) {
        validateLine(sections);
        this.sections = new Sections(sections);
    }

    public Section updateOverlappedSection(final SectionParam params) {
        validateOnlyOneStationExistInLine(params);

        if (sections.isDownDirectionSectionExist(params.getUpStation().getId())) {
            Section overlappedSection = sections.getDownDirectionSection(
                params.getUpStation().getId());
            return updateOverlappedDownDirectionSection(overlappedSection, params);
        }

        if (sections.isUpDirectionSectionExist(params.getDownStation().getId())) {
            Section overlappedSection = sections.getUpDirectionSection(
                params.getDownStation().getId());
            return updateOverlappedUpDirectionSection(overlappedSection, params);
        }

        throw new IllegalSectionException("연결된 구간이 존재하지 않습니다.");
    }

    public boolean isLastStation(final long stationId) {
        return sections.isLastStation(stationId);
    }

    public boolean shouldUpdate(final SectionParam params) {
        validateOnlyOneStationExistInLine(params);

        boolean isUpStationOverlapped =
            sections.isStationExist(params.getUpStation().getId()) &&
                !sections.isEndStation(params.getUpStation().getId());
        boolean isDownStationOverlapped =
            sections.isStationExist(params.getDownStation().getId()) &&
                !sections.isStartStation(params.getDownStation().getId());

        return isUpStationOverlapped || isDownStationOverlapped;
    }

    public Section findUpDirectionSection(long stationId) {
        return sections.getUpDirectionSection(stationId);
    }

    public Section findDownDirectionSection(long stationId) {
        return sections.getDownDirectionSection(stationId);
    }

    public Section getLastSection(long stationId) {
        if (sections.isLastStation(stationId)) {
            return sections.getConnectedSection(stationId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalSectionException("연결된 구간이 존재하지 않습니다."));
        }

        throw new IllegalStationsException("종점 구간이 포함된 역이 아닙니다.");
    }

    public Line getLine() {
        return sections.getAll()
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
        boolean upStationExist = sections.isStationExist(params.getUpStation().getId());
        boolean downStationExist = sections.isStationExist(params.getDownStation().getId());
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
        SectionUpdater sectionUpdater = (SectionUpdater) o;
        return Objects.equals(sections, sectionUpdater.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }

    @Override
    public String toString() {
        return "Sections{" +
            "connectedSection=" + sections +
            '}';
    }
}
