package subway.domain;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineSections)) {
            return false;
        }
        LineSections that = (LineSections) o;
        return Objects.equals(line, that.line) && Objects.equals(sections,
            that.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, sections);
    }

    @Override
    public String toString() {
        return "LineSections{" +
            "line=" + line +
            ", sections=" + sections +
            '}';
    }
}
