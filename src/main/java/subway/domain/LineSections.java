package subway.domain;

import java.util.List;
import java.util.Objects;
import subway.vo.SectionAdditionResult;

public class LineSections {

    private final Line line;
    private final Sections sections;

    public LineSections(Line line, Sections sections) {
        this.line = line;
        this.sections = sections;

        validateAllSectionsBelongToThisLine();
    }

    private void validateAllSectionsBelongToThisLine() {
        if (!this.sections.isSectionsAllBelongTo(this.line)) {
            throw new IllegalArgumentException(
                "현재 구간들은 해당 노선에 속하지 않습니다. current line: " + this.line);
        }
    }

    public LineSections(Line line, Section section) {
        this(line, new Sections(List.of(section)));
    }

    public SectionAdditionResult add(Section section) {
        return sections.add(new Section(this.line, section));
    }

    public Section removeLast(Station station) {
        return sections.removeLast(station);
    }

    public Line getLine() {
        return line;
    }

    public Sections getSections() {
        return sections;
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
