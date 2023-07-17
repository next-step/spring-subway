package subway.domain;

import java.util.List;
import java.util.Objects;

public class Sections {

    private static final String SAME_STATION_EXCEPTION_MESSAGE = "새로운 구간의 상행역은 기존 하행 종점역과 같아야 합니다.";
    private static final String DUPLICATED_EXCEPTION_MESSAGE = "새로운 구간의 하행역은 기존 노선에 등록되어 있지 않은 역이어야 합니다.";
    private static final String EMPTY_EXCEPTION_MESSAGE = "최소 1개 이상의 구간이 있어야 합니다.";

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        validateEmpty(sections);
        this.sections = sections;
    }

    public void insert(final Section newSection) {
        validateSameStation(newSection);
        validateDuplicated(newSection);

        sections.add(newSection);
    }

    private static void validateEmpty(final List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException(EMPTY_EXCEPTION_MESSAGE);
        }
    }

    private void validateDuplicated(final Section newSection) {
        Long newDownStationId = newSection.getDownStationId();

        // TODO : Set으로 시간복잡도 줄이는 것 논의하기
        boolean isIncluded = sections.stream()
                .anyMatch(section -> section.getUpStationId() == newDownStationId);

        if (isIncluded) {
            throw new IllegalArgumentException(DUPLICATED_EXCEPTION_MESSAGE);
        }
    }

    private void validateSameStation(final Section newSection) {
        if (!Objects.equals(newSection.getUpStationId(), sections.get(sections.size() - 1).getDownStationId())) {
            throw new IllegalArgumentException(SAME_STATION_EXCEPTION_MESSAGE);
        }
    }
}
