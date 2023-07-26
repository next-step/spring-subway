package subway.domain.vo;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import subway.domain.Section;

public class SectionDeleteVo {

    private final List<Section> deleteSections;
    private final Optional<Section> combinedSection;

    public SectionDeleteVo(final List<Section> deleteSections,
            final Optional<Section> combinedSection) {
        this.deleteSections = deleteSections;
        this.combinedSection = combinedSection;
    }

    public List<Section> getDeleteSections() {
        return deleteSections;
    }

    public Optional<Section> getCombinedSection() {
        return combinedSection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SectionDeleteVo that = (SectionDeleteVo) o;
        return Objects.equals(deleteSections, that.deleteSections)
                && Objects.equals(combinedSection, that.combinedSection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deleteSections, combinedSection);
    }

    @Override
    public String toString() {
        return "SectionDeleteVo{" +
                "deleteSections=" + deleteSections +
                ", combinedSection=" + combinedSection +
                '}';
    }
}
