package subway.domain;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import subway.domain.response.SectionDisconnectResponse;
import subway.domain.status.SectionExceptionStatus;
import subway.util.Assert;

public class Section {

    private final Long id;
    private Distance distance;
    private Station upStation;
    private Station downStation;
    private Section upSection;
    private Section downSection;

    private Section(Builder builder) {
        validate(builder);

        this.upStation = builder.upStation;
        this.downStation = builder.downStation;
        this.distance = new Distance(builder.distance);
        this.id = builder.id;
        this.upSection = builder.upSection;
        this.downSection = builder.downSection;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validate(Builder builder) {
        Assert.notNull(builder.upStation, () -> "upStation은 null이 될 수 없습니다.",
                SectionExceptionStatus.NULL_STATION.getStatus());
        Assert.notNull(builder.downStation, () -> "downStation은 null이 될 수 없습니다.",
                SectionExceptionStatus.NULL_STATION.getStatus());
        Assert.isTrue(!builder.upStation.equals(builder.downStation),
                () -> MessageFormat.format("upStation\"{0}\"과 downStation\"{1}\"은 같을 수 없습니다.", upStation, downStation),
                SectionExceptionStatus.DUPLICATE_STATION.getStatus());
    }

    public Section connectSection(Section requestSection) {
        Assert.notNull(requestSection, () -> "requestSection은 null이 될 수 없습니다",
                SectionExceptionStatus.NULL_REQUEST_SECTION.getStatus());
        if (isConnectUpSection(requestSection)) {
            return connectUpSection(requestSection);
        }
        if (isConnectMiddleUpSection(requestSection)) {
            return connectMiddleUpSection(requestSection);
        }
        if (isConnectMiddleDownSection(requestSection)) {
            return connectMiddleDownSection(requestSection);
        }
        if (isConnectDownSection(requestSection)) {
            return connectDownSection(requestSection);
        }
        return connectToNextSection(requestSection);
    }

    private boolean isConnectUpSection(Section requestSection) {
        return upSection == null && upStation.equals(requestSection.getDownStation());
    }

    private boolean isConnectMiddleUpSection(Section requestSection) {
        return upStation.equals(requestSection.getUpStation());
    }

    private boolean isConnectMiddleDownSection(Section requestSection) {
        return downStation.equals(requestSection.getDownStation());
    }

    private boolean isConnectDownSection(Section requestSection) {
        return downSection == null && downStation.equals(requestSection.getUpStation());
    }

    private Section connectToNextSection(Section requestSection) {
        Assert.notNull(downSection,
                () -> MessageFormat.format("line에 requestSection \"{0}\"을 연결할 수 없습니다.", requestSection),
                SectionExceptionStatus.CANNOT_CONNECT_SECTION.getStatus());

        return downSection.connectSection(requestSection);
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
                .distance(this.distance.value - requestSection.getDistance())
                .build();

        this.downStation = requestSection.downStation;
        if (this.downSection != null) {
            this.downSection.upSection = newDownSection;
        }
        this.downSection = newDownSection;
        this.distance = new Distance(requestSection.getDistance());
        return newDownSection;
    }

    private Section connectMiddleDownSection(Section requestSection) {
        Section newUpSection = Section.builder()
                .id(requestSection.getId())
                .upSection(this.upSection)
                .downSection(this)
                .upStation(this.upStation)
                .downStation(requestSection.upStation)
                .distance(this.distance.value - requestSection.getDistance())
                .build();

        this.upStation = requestSection.upStation;
        if (this.upSection != null) {
            this.upSection.downSection = newUpSection;
        }
        this.upSection = newUpSection;
        this.distance = new Distance(requestSection.getDistance());
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

    public SectionDisconnectResponse disconnectStation(Station requestStation) {
        if (isDisconnectUpStation(requestStation)) {
            return disconnectUpSection();
        }
        if (isDisconnectMiddleStation(requestStation)) {
            return disconnectMiddleSection();
        }
        if (isDisconnectDownStation(requestStation)) {
            return disconnectDownSection();
        }
        return disconnectToNextSection(requestStation);
    }

    private boolean isDisconnectUpStation(Station requestStation) {
        return upStation.equals(requestStation) && upSection == null;
    }

    private boolean isDisconnectMiddleStation(Station requestStation) {
        return downStation.equals(requestStation) && downSection != null;
    }

    private boolean isDisconnectDownStation(Station requestStation) {
        return downStation.equals(requestStation) && downSection == null;
    }

    private SectionDisconnectResponse disconnectToNextSection(Station station) {
        Assert.notNull(downSection, () -> MessageFormat.format("삭제 가능한 section을 찾을 수 없습니다. station \"{0}\"", station),
                SectionExceptionStatus.CANNOT_DISCONNECT_SECTION.getStatus());
        return downSection.disconnectStation(station);
    }

    private SectionDisconnectResponse disconnectUpSection() {
        downSection.upSection = null;
        return new SectionDisconnectResponse(this, List.of(downSection));
    }

    private SectionDisconnectResponse disconnectMiddleSection() {
        distance = new Distance(distance.value + downSection.getDistance());
        downStation = downSection.downStation;
        if (isSectionHasDescendant()) {
            SectionDisconnectResponse sectionDisconnectResponse = new SectionDisconnectResponse(downSection,
                    List.of(this, downSection.downSection));
            downSection = downSection.downSection;
            downSection.upSection = this;
            return sectionDisconnectResponse;
        }
        SectionDisconnectResponse sectionDisconnectResponse = new SectionDisconnectResponse(downSection, List.of(this));
        downSection = null;
        return sectionDisconnectResponse;
    }

    private boolean isSectionHasDescendant() {
        return downSection.downSection != null;
    }

    private SectionDisconnectResponse disconnectDownSection() {
        upSection.downSection = null;
        return new SectionDisconnectResponse(this, List.of(upSection));
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
        return distance.value;
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
