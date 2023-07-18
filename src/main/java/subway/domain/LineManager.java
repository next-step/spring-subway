package subway.domain;

import java.util.List;

public class LineManager {

    private final Line line;
    private final List<Section> sections;

    public LineManager(Line line, List<Section> sections) {
        this.line = line;
        this.sections = sections;
    }

    public void connectDownSection(Section downSection) {
        Section lineDownSection = sections.get(0).findDownSection();
        lineDownSection.connectDownSection(downSection);
    }
}
