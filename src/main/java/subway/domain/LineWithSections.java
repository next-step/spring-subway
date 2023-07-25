package subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import subway.exception.SubwayException;

public class LineWithSections {

    private final List<LineWithSection> values;

    public LineWithSections(final List<LineWithSection> values) {
        validatesSectionHasSameLine(values);
        this.values = values;
    }

    public Line getLine() {
        return values.get(0).getLine();
    }

    public List<Long> getSortedStationIds() {
        return new Sections(values.stream()
                .map(LineWithSection::getSection)
                .collect(Collectors.toList()))
                .getStationIds();
    }

    private void validatesSectionHasSameLine(final List<LineWithSection> values) {
        final Line line = values.get(0).getLine();
        if (values.stream().anyMatch(value -> !value.getLine().equals(line))) {
            throw new SubwayException("해당 구간 정보에 다른 노선의 구간이 포함되어 있습니다.");
        }
    }
}
