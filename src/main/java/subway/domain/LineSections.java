package subway.domain;

import java.util.List;

public class LineSections {

    private final Line line;
    private final Sections sections;

    public LineSections(Line line, Section section) {
        if (!section.belongTo(line)) {
            throw new IllegalArgumentException("현재 구간은 해당 노선에 속하지 않습니다. current line: " + line + ", section: " + section);
        }

        this.line = line;
        this.sections = new Sections(List.of(section));
    }
}
