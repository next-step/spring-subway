package subway.domain;

import java.util.List;
import java.util.Objects;
import subway.dto.SectionAdditionResult;
import subway.dto.SectionRemovalResult;

public class LineSections {

    private final Line line;
    private final Sections sections;

    public LineSections(Line line, Sections sections) {
        this.line = line;
        this.sections = sections;

        validateAllSectionsBelongToThisLine();
    }

    public LineSections(Line line, Section section) {
        this(line, new Sections(List.of(section)));
    }

    private void validateAllSectionsBelongToThisLine() {
        if (!this.sections.isSectionsAllBelongTo(this.line)) {
            throw new IllegalArgumentException(
                "현재 구간들은 해당 노선에 속하지 않습니다. current line: " + this.line);
        }
    }

    public SectionAdditionResult add(Section section) {
        validateSectionInLine(section);
        return sections.add(section);
    }

    private void validateSectionInLine(Section section) {
        if (!section.belongTo(this.line)) {
            throw new IllegalArgumentException("추가할 구간은 다른 노선에 속해있습니다. 현재 노선: " + line + " 추가할 구간: " + section);
        }
    }

    public SectionRemovalResult remove(Station station) {
        return sections.remove(station);
    }

    public List<Station> getAllStations() {
        return this.sections.getStations();
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
