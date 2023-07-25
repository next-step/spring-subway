package subway.domain;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.util.Assert;
import subway.domain.response.SectionDisconnectResponse;

public class Section {

    private static final int MIN_DISTANCE_SIZE = 0;

    private final Long id;
    private int distance;
    private Station upStation;
    private Station downStation;
    private Section upSection;
    private Section downSection;

    private Section(Builder builder) {
        validate(builder);

        this.upStation = builder.upStation;
        this.downStation = builder.downStation;
        this.distance = builder.distance;
        this.id = builder.id;
        this.upSection = builder.upSection;
        this.downSection = builder.downSection;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validate(Builder builder) {
        Assert.notNull(builder.upStation, () -> "upStation은 null이 될 수 없습니다.");
        Assert.notNull(builder.downStation, () -> "downStation은 null이 될 수 없습니다.");
        Assert.isTrue(builder.distance > MIN_DISTANCE_SIZE,
                () -> MessageFormat.format("distance \"{0}\"는 0 이하가 될 수 없습니다.", builder.distance));
        Assert.isTrue(!builder.upStation.equals(builder.downStation),
                () -> MessageFormat.format("upStation\"{0}\"과 downStation\"{1}\"은 같을 수 없습니다.", upStation, downStation));
    }

    public Section connectSection(Section requestSection) {
        Assert.notNull(requestSection, () -> "requestSection은 null이 될 수 없습니다");
        Optional<SectionConnector> sectionConnectorOptional = SectionConnector
                .findSectionConnector(this, requestSection);

        if (sectionConnectorOptional.isEmpty()) {
            return connectToNextSection(requestSection);
        }

        return connectToCurrentSection(sectionConnectorOptional.get(), requestSection);
    }


    private Section connectToNextSection(Section requestSection) {
        Assert.notNull(downSection,
                () -> MessageFormat.format("line에 requestSection \"{0}\"을 연결할 수 없습니다.", requestSection));

        return downSection.connectSection(requestSection);
    }

    private Section connectToCurrentSection(SectionConnector sectionConnector, Section requestSection) {
        if (sectionConnector == SectionConnector.UP) {
            return connectUpSection(requestSection);
        }
        if (sectionConnector == SectionConnector.MIDDLE_UP) {
            return connectMiddleUpSection(requestSection);
        }
        if (sectionConnector == SectionConnector.MIDDLE_DOWN) {
            return connectMiddleDownSection(requestSection);
        }
        return connectDownSection(requestSection);
    }

    private Section connectDownSection(Section requestSection) {
        this.downSection = requestSection;
        requestSection.upSection = this;
        return downSection;
    }

    private Section connectUpSection(Section requestSection) {
        this.upSection = requestSection;
        requestSection.downSection = this;
        return requestSection;
    }

    private Section connectMiddleUpSection(Section requestSection) {
        Section newDownSection = Section.builder()
                .id(requestSection.getId())
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
        return newDownSection;
    }

    private Section connectMiddleDownSection(Section requestSection) {
        Section newUpSection = Section.builder()
                .id(requestSection.getId())
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
        return newUpSection;
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

    public SectionDisconnectResponse disconnectStation(Station station) {
        if (isUpStation(station)) {
            downSection.upSection = null;
            return new SectionDisconnectResponse(this, List.of(downSection));
        }
        if (isDownStation(station)) {
            upSection.downSection = null;
            return new SectionDisconnectResponse(this, List.of(upSection));
        }
        if (isMiddleStation(station)) {
            distance += downSection.distance;
            downStation = downSection.downStation;
            if (downSection.downSection != null) {
                SectionDisconnectResponse sectionDisconnectResponse = new SectionDisconnectResponse(downSection,
                        List.of(this, downSection.downSection));
                downSection = downSection.downSection;
                downSection.upSection = this;
                return sectionDisconnectResponse;
            }
            SectionDisconnectResponse sectionDisconnectResponse = new SectionDisconnectResponse(downSection,
                    List.of(this));
            downSection = null;
            return sectionDisconnectResponse;
        }
        if (downSection == null) {
            throw new IllegalStateException(
                    MessageFormat.format("삭제 가능한 section을 찾을 수 없습니다. station \"{0}\"", station));
        }
        return downSection.disconnectStation(station);
    }

    private boolean isUpStation(Station station) {
        return station.equals(upStation) && upSection == null;
    }

    private boolean isDownStation(Station station) {
        return station.equals(downStation) && downSection == null;
    }

    private boolean isMiddleStation(Station station) {
        return station.equals(downStation) && downSection != null;
    }

    public Long getId() {
        return id;
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

    public int getDistance() {
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
                section.distance) && Objects.equals(downSection,
                section.downSection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance, downSection);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                ", downSection=" + downSection +
                '}';
    }

    public static class Builder {

        private Long id;
        private Station upStation;
        private Station downStation;
        private Section upSection;
        private Section downSection;
        private int distance;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
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

        public Builder distance(int distance) {
            this.distance = distance;
            return this;
        }

        public Section build() {
            return new Section(this);
        }
    }
}
