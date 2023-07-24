package subway.domain;

public class LineWithSection {

    private final Line line;
    private final Section section;

    public LineWithSection(final Line line, final Section section) {
        this.line = line;
        this.section = section;
    }

    public Line getLine() {
        return this.line;
    }

    public Section getSection() {
        return this.section;
    }
}
