package subway.domain;

import java.util.List;

public class LineSections {

    private final Line line;
    private final Sections sections;

    public LineSections(Line line, Sections sections) {
        sections.validateSectionsBelongToLine(line);
        this.line = line;
        this.sections = sections;
    }

    public LineSections(Line line, Section section) {
        this(line, new Sections(List.of(section)));
    }

    public Section addLast(Section section) {
        return sections.addLast(section);
    }

    public Section removeLast(Station station) {
        return sections.removeLast(station);
    }
}
