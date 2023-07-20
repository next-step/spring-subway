package subway.domain;

import java.text.MessageFormat;
import java.util.Objects;
import org.springframework.util.Assert;

public class Section {

    private final Long id;
    private final Line line;
    private Integer distance;
    private Station upStation;
    private Station downStation;
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

    public void connectDownSection(Section requestSection) {
        Assert.notNull(requestSection, () -> "requestSection null이 될 수 없습니다.");
        Assert.isTrue(requestSection.upStation.equals(downStation), () -> MessageFormat.format(
                "추가되는 requestSection.upStation은 현재의 section.downStation과 동일해야합니다. requestSection.upStation \"{0}\" current.downStation \"{1}\"",
                requestSection.upStation, downStation));

        this.downSection = requestSection;
        requestSection.upSection = this;
    }

    void connectSection(Section requestSection) {
        Assert.notNull(requestSection, () -> "requestSection은 null이 될 수 없습니다");
        SectionConnector.findSectionConnector(this, requestSection)
                .ifPresentOrElse(sectionConnector1 -> sectionConnector1.connectSection(this, requestSection),
                        () -> connectSectionIfDownSectionPresent(requestSection));
    }

    private void connectSectionIfDownSectionPresent(Section requestSection) {
        Assert.notNull(downSection,
                () -> MessageFormat.format("line에 requestSection \"{0}\"을 연결할 수 없습니다.", requestSection));

        downSection.connectSection(requestSection);
    }

    void connectUpSection(Section requestSection) {
        this.upSection = requestSection;
        requestSection.downSection = this;
    }

    void connectMiddleUpSection(Section requestSection) {
        Section newDownSection = Section.builder()
                .id(requestSection.getId())
                .line(line)
                .upSection(this)
                .downSection(this.downSection)
                .upStation(requestSection.downStation)
                .downStation(this.downStation)
                .distance(this.distance - requestSection.getDistance())
                .build();

        this.downStation = requestSection.downStation;
        if (this.downSection != null) {
            this.downSection.upSection = newDownSection;
        }
        this.downSection = newDownSection;
        this.distance = requestSection.getDistance();
    }

    void connectMiddleDownSection(Section requestSection) {
        Section newUpSection = Section.builder()
                .id(requestSection.getId())
                .line(line)
                .upSection(this.upSection)
                .downSection(this)
                .upStation(this.upStation)
                .downStation(requestSection.upStation)
                .distance(this.distance - requestSection.getDistance())
                .build();

        this.upStation = requestSection.upStation;
        if (this.upSection != null) {
            this.upSection.downSection = newUpSection;
        }
        this.upSection = newUpSection;
        this.distance = requestSection.getDistance();
    }

    public Section findDownSection() {
        if (downSection == null) {
            return this;
        }
        return downSection.findDownSection();
    }

    public Section findUpSection() {
        if (upSection == null) {
            return this;
        }
        return upSection.findUpSection();
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

    public void disconnectDownSection() {
        Assert.notNull(downSection, () -> "downSection이 null 일때, \"disconnectDownSection()\" 를 호출할 수 없습니다");
        downSection.upSection = null;
        downSection = null;
    }

    public static class Builder {

        protected Long id;
        protected Line line;
        protected Station upStation;
        protected Station downStation;
        protected Section upSection;
        protected Section downSection;
        protected Integer distance;

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
