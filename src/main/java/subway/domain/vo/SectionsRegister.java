package subway.domain.vo;

import java.util.Optional;
import subway.domain.Section;

public class SectionsRegister {

    private final Section addSection;

    private final Optional<Section> updateSection;

    public SectionsRegister(Section addSection, Section updateSection) {
        this.addSection = addSection;
        this.updateSection = Optional.of(updateSection);
    }

    public SectionsRegister(Section addSection) {
        this.addSection = addSection;
        this.updateSection = Optional.empty();
    }

    public Section getAddSection() {
        return addSection;
    }

    public Optional<Section> getUpdateSection() {
        return updateSection;
    }
}
