package subway.domain;

import subway.exception.SubwayIllegalArgumentException;
import subway.utils.SetUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DistinctSections {

    private final Set<Section> values;

    public DistinctSections(final List<Section> sections) {
        final Set<Section> distinctSections = new HashSet<>(sections);
        validateDoesNotExistDuplicateSection(sections, distinctSections);

        this.values = distinctSections;
    }

    private void validateDoesNotExistDuplicateSection(final List<Section> values, final Set<Section> sections) {
        if (sections.size() != values.size()) {
            throw new SubwayIllegalArgumentException("중복된 구간이 존재합니다.");
        }
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public Set<Long> getSectionIds() {
        return SetUtils.union(
                SetUtils.toSet(values, Section::getUpStationId),
                SetUtils.toSet(values, Section::getDownStationId)
        );
    }

    public Set<Section> getSections() {
        return values;
    }
}
