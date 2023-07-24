package subway.domain;

import java.util.List;
import java.util.stream.Collectors;

public class LineWithSections {

    private final List<LineWithSection> values;

    public LineWithSections(final List<LineWithSection> values) {
        this.values = values;
    }

    public Line getLine() {
        return values.get(0).getLine();
    }

    public List<Long> getSortedStationIds() {
        return new Sections(values.stream()
                .map(LineWithSection::getSection)
                .collect(Collectors.toList()))
                .getSortedStationIds();
    }
}
