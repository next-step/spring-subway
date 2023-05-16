package subway.domain;

import subway.exception.ErrorType;
import subway.exception.ServiceException;

public class Section {

    private Long id;
    private Long lineId;
    private Station downStation;
    private Station upStation;
    private Integer distance;

    private Section(Long id, Long lineId, Station downStation, Station upStation, Integer distance) {
        validateDuplicateStation(downStation, upStation);
        this.id = id;
        this.lineId = lineId;
        this.downStation = downStation;
        this.upStation = upStation;
        this.distance = distance;
    }

    private void validateDuplicateStation(Station downStation, Station upStation) {
        if (downStation.getId() == upStation.getId()) {
            throw new ServiceException(ErrorType.VALIDATE_DUPLICATE_SECTION);
        }

    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public long getDownStationId() {
        return downStation.getId();
    }

    public long getUpStationId() {
        return upStation.getId();
    }

    public static SectionBuilder builder() {
        return new SectionBuilder();
    }

    public static class SectionBuilder {

        private Long id;
        private Long lineId;
        private Station downStation;
        private Station upStation;
        private Integer distance;

        public SectionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SectionBuilder lineId(Long lineId) {
            this.lineId = lineId;
            return this;
        }

        public SectionBuilder downStation(Station downStation) {
            this.downStation = downStation;
            return this;
        }

        public SectionBuilder upStation(Station upStation) {
            this.upStation = upStation;
            return this;
        }

        public SectionBuilder distance(Integer distance) {
            this.distance = distance;
            return this;
        }

        public Section build() {
            return new Section(id, lineId, downStation, upStation, distance);
        }
    }

}
