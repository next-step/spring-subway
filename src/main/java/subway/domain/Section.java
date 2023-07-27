package subway.domain;

import subway.exception.SectionException;
import subway.exception.StationException;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

public class Section {

    private static final int MIN_DISTANCE_SIZE = 1;

    private final Long id;

    private Integer distance;
    private Station upStation;
    private Station downStation;
    private Section upSection;
    private Section downSection;

    private Section(Builder builder) {
        validate(builder);

        this.id = builder.id;
        this.distance = builder.distance;
        this.upStation = builder.upStation;
        this.downStation = builder.downStation;
        this.upSection = builder.upSection;
        this.downSection = builder.downSection;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void validate(Builder builder) {
        validateNullStations(builder);
        validateSameStations(builder);
        validateDistance(builder);
    }

    private void validateNullStations(Builder builder) {
        if (builder.upStation == null) {
            throw new StationException("station이 존재하지 않습니다.");
        }

        if (builder.downStation == null) {
            throw new StationException("downStation이 존재하지 않습니다.");
        }
    }

    private void validateSameStations(Builder builder) {
        if (builder.upStation.equals(builder.downStation)) {
            throw new StationException(
                    MessageFormat.format("upStation\"{0}\"과 downStation\"{1}\"은 같을 수 없습니다.", builder.upStation, builder.downStation)
            );
        }
    }

    private void validateDistance(Builder builder) {
        if (builder.distance < MIN_DISTANCE_SIZE) {
            throw new SectionException(
                    MessageFormat.format("distance \"{0}\"는 0 이하가 될 수 없습니다.", builder.distance)
            );
        }
    }

    public Optional<Section> findUpdateSectionWhenConnect(final Section newSection) {
        if (newSection == null) {
            throw new SectionException("newSection이 존재하지 않습니다.");
        }
        if (upSection == null && upStation.equals(newSection.getDownStation())) {
            return Optional.empty();
        }
        if (downSection == null && downStation.equals(newSection.getUpStation())) {
            return Optional.empty();
        }
        if (upStation.equals(newSection.getUpStation())) {
            return Optional.ofNullable(getUpdateUpSectionWhenConnect(newSection));
        }
        if (downStation.equals(newSection.getDownStation())) {
            return Optional.of(getUpdateDownSectionWhenConnect(newSection));
        }

        return findUpdateSectionWhenConnectIfPresent(newSection);
    }

    public Optional<Section> findUpdateSectionWhenDisconnect(Station removeStation) {
        if (removeStation == null) {
            throw new StationException("removeStation이 존재하지 않습니다");
        }
        if (upSection == null && upStation.equals(removeStation)) {
            return Optional.empty();
        }
        if (downSection == null && downStation.equals(removeStation)) {
            return Optional.empty();
        }
        if (upSection != null && upStation.equals(removeStation)) {
            return Optional.ofNullable(getUpdateSectionWhenDisconnect());
        }

        return findUpdateSectionWhenDisconnectIfPresent(removeStation);
    }

    private Optional<Section> findUpdateSectionWhenDisconnectIfPresent(final Station removeStation) {
        if (downSection == null) {
            throw new SectionException(
                    MessageFormat.format("line에 removeStation \"{0}\"을 제거할 수 없습니다.", removeStation)
            );
        }

        return downSection.findUpdateSectionWhenDisconnect(removeStation);
    }

    private Section getUpdateSectionWhenDisconnect() {
        return Section.builder()
                .upStation(upSection.upStation)
                .downStation(downStation)
                .distance(distance + upSection.distance)
                .id(id)
                .build();
    }

    private Section getUpdateDownSectionWhenConnect(Section newSection) {
        return Section.builder()
                .upStation(upStation)
                .downStation(newSection.upStation)
                .distance(distance - newSection.distance)
                .id(id)
                .build();
    }

    private Section getUpdateUpSectionWhenConnect(Section newSection) {
        return Section.builder()
                .upStation(newSection.downStation)
                .downStation(downStation)
                .distance(distance - newSection.distance)
                .id(id)
                .build();
    }

    private Optional<Section> findUpdateSectionWhenConnectIfPresent(final Section newSection) {
        if (downSection == null) {
            throw new SectionException(
                    MessageFormat.format("line에 requestSection \"{0}\"을 연결할 수 없습니다.", newSection)
            );
        }

        return downSection.findUpdateSectionWhenConnect(newSection);
    }

    public void connectDownSection(final Section requestSection) {
        if (requestSection == null) {
            throw new SectionException("requestSection이 존재하지 않습니다.");
        }
        if (!downStation.equals(requestSection.upStation)) {
            throw new SectionException("middle Station이 달라 연결할 수 없습니다.");
        }
        this.downSection = requestSection;
        requestSection.upSection = this;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public static class Builder {

        protected Long id;
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
