package subway.domain;

import java.text.MessageFormat;
import java.util.Objects;
import org.springframework.util.Assert;

public class Section {

    private static final Section NOT_CONNECTED_SECTION = new Section();

    private final Station upStation;
    private final Station downStation;
    private Section upSection = NOT_CONNECTED_SECTION;
    private Section downSection = NOT_CONNECTED_SECTION;

    public Section(Station upStation, Station downStation) {
        Assert.notNull(upStation, () -> "upStation은 null이 될 수 없습니다.");
        Assert.notNull(downStation, () -> "downStation은 null이 될 수 없습니다.");

        this.upStation = upStation;
        this.downStation = downStation;
    }

    private Section() {
        upStation = null;
        downStation = null;
    }

    void connectDownSection(Section downSection) {
        Assert.notNull(downSection, () -> "downSection은 null이 될 수 없습니다.");
        Assert.isTrue(downSection.upStation == downStation, () -> MessageFormat.format(
                "추가되는 downSection.upStation은 현재의 section.downStation과 동일해야합니다. downSection.upStation \"{0}\" current.downStation \"{1}\"",
                downSection.upStation, downStation));

        this.downSection = downSection;
        downSection.upSection = this;
    }

    Section getDownsection() {
        return downSection;
    }

    Section getUpSection() {
        return upSection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(upStation, section.upStation) && Objects.equals(downStation,
                section.downStation) && Objects.equals(upSection, section.upSection) && Objects.equals(
                downSection, section.downSection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, upSection, downSection);
    }

    @Override
    public String toString() {
        return "Section{" +
                "upStation=" + upStation +
                ", downStation=" + downStation +
                ", upSection=" + upSection +
                ", downSection=" + downSection +
                '}';
    }
}
