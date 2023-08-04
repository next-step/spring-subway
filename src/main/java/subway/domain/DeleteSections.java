package subway.domain;

import java.util.List;
import subway.exception.ErrorCode;
import subway.exception.StationException;

public class DeleteSections {

    private final Sections deleteSections;

    public DeleteSections(final List<Section> deleteSections) {
        validate(deleteSections);

        this.deleteSections = new Sections(deleteSections);
    }

    private void validate(final List<Section> deleteSections) {
        if (deleteSections.isEmpty()) {
            throw new StationException(ErrorCode.EMPTY_SECTION, "삭제할 역이 존재하지 않습니다.");
        }
    }

    public boolean isKindOfMidDeletion() {
        return deleteSections.hasSize(2);
    }

    public Section newSection() {
        return deleteSections.connectTerminals();
    }

    public List<Long> getIds() {
        return deleteSections.getIds();
    }
}
