package subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sections {

    private final List<Section> sections;

    public Sections(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }
}
