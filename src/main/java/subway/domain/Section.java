package subway.domain;

import java.text.MessageFormat;
import java.util.Objects;
import org.springframework.util.Assert;

public class Section {

    private final Long id;
    private final Station upStation;
    private final Station downStation;
    private final Integer distance;
    private final Line line;
    private Section upSection;
    private Section downSection;

    private Section(Builder builder) {
        Assert.notNull(builder.upStation, () -> "upStation은 null이 될 수 없습니다.");
        Assert.notNull(builder.downStation, () -> "downStation은 null이 될 수 없습니다.");
        Assert.isTrue(builder.distance > 0,
                () -> MessageFormat.format("distance \"{0}\"는 0 이하가 될 수 없습니다.", builder.distance));

        this.upStation = builder.upStation;
        this.downStation = builder.downStation;
        this.distance = builder.distance;
        this.line = builder.line;
        this.id = builder.id;
        this.upSection = builder.upSection;
        this.downSection = builder.downSection;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void connectDownSection(Section downSection) {
        Assert.notNull(downSection, () -> "downSection은 null이 될 수 없습니다.");
        Assert.isTrue(downSection.upStation.equals(downStation), () -> MessageFormat.format(
                "추가되는 downSection.upStation은 현재의 section.downStation과 동일해야합니다. downSection.upStation \"{0}\" current.downStation \"{1}\"",
                downSection.upStation, downStation));

        this.downSection = downSection;
        downSection.upSection = this;
    }

    public Section findDownSection() {
        if (downSection == null) {
            return this;
        }
        return downSection.findDownSection();
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Section getDownSection() {
        return downSection;
    }

    public Section getUpSection() {
        return upSection;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
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
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation)
                && Objects.equals(downStation, section.downStation) && Objects.equals(distance,
                section.distance) && Objects.equals(line, section.line) && Objects.equals(downSection,
                section.downSection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, line, downSection);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                ", line=" + line +
                ", downSection=" + downSection +
                '}';
    }

    public static final class Builder {

        private Long id;
        private Line line;
        private Station upStation;
        private Station downStation;
        private Section upSection;
        private Section downSection;
        private Integer distance;

        private Builder() {

        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder line(Line line) {
            this.line = line;
            return this;
        }

        public Builder upStation(Station upStation) {
            this.upStation = upStation;
            return this;
        }

        public Builder downStation(Station downStation) {
            this.downStation = downStation;
            return this;
        }

        public Builder upSection(Section upSection) {
            this.upSection = upSection;
            return this;
        }

        public Builder downSection(Section downSection) {
            this.downSection = downSection;
            return this;
        }

        public Builder distance(Integer distance) {
            this.distance = distance;
            return this;
        }

        public Section build() {
            return new Section(this);
        }

    }
}
