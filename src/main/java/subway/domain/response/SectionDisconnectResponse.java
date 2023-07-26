package subway.domain.response;

import java.util.List;
import java.util.Objects;
import subway.domain.Section;

public class SectionDisconnectResponse {

    private final Section deletedSection;
    private final List<Section> updatedSections;

    public SectionDisconnectResponse(Section deletedSection, List<Section> updatedSections) {
        this.deletedSection = deletedSection;
        this.updatedSections = updatedSections;
    }

    public Section getDeletedSection() {
        return deletedSection;
    }

    public List<Section> getUpdatedSections() {
        return updatedSections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SectionDisconnectResponse)) {
            return false;
        }
        SectionDisconnectResponse that = (SectionDisconnectResponse) o;
        return Objects.equals(deletedSection, that.deletedSection) && Objects.equals(updatedSections,
                that.updatedSections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deletedSection, updatedSections);
    }

    @Override
    public String toString() {
        return "SectionDisconnectResponse{" +
                "deletedSection=" + deletedSection +
                ", updatedSections=" + updatedSections +
                '}';
    }
}
