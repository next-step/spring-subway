package subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import subway.exception.ErrorCode;
import subway.exception.StationException;

public class DeleteSections {

    private static final String NOT_EXISTS_STATION_EXCEPTION_MESSAGE = "삭제할 역이 존재하지 않습니다.";

    private final List<Section> deleteSections;

    public DeleteSections(final List<Section> deleteSections) {
        validate(deleteSections);

        this.deleteSections = deleteSections;
    }

    private void validate(final List<Section> deleteSections) {
        if (deleteSections.isEmpty()) {
            throw new StationException(ErrorCode.EMPTY_SECTION, NOT_EXISTS_STATION_EXCEPTION_MESSAGE);
        }
    }

    public boolean isKindOfMidDeletion() {
        return deleteSections.size() == 2;
    }

    public Section newSection() {
        final Section firstSection = deleteSections.get(0);
        final Section secondSection = deleteSections.get(1);

        if (firstSection.isInOrder(secondSection)) {
            return new Section(
                    firstSection.getUpStation(), secondSection.getDownStation(), firstSection.distanceSum(secondSection));
        }

        return new Section(
                secondSection.getUpStation(), firstSection.getDownStation(), secondSection.distanceSum(firstSection));
    }

    public List<Long> getIds() {
        return deleteSections.stream()
                .map(Section::getId)
                .collect(Collectors.toUnmodifiableList());
    }
}
