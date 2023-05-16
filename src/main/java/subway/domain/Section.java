package subway.domain;

import subway.exception.ErrorType;
import subway.exception.ServiceException;

public class Section {

    private Long id;
    private Long lineId;
    private Long downStationId;
    private Long upStationId;
    private Integer distance;

    private Section(Long id, Long lineId, Long downStationId, Long upStationId, Integer distance) {
        validateDuplicateStation(downStationId, upStationId);
        this.id = id;
        this.lineId = lineId;
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }

    private void validateDuplicateStation(long downStationId, long upStationId) {
        if (downStationId == upStationId) {
            throw new ServiceException(ErrorType.VALIDATE_DUPLICATE_SECTION);
        }

    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Integer getDistance() {
        return distance;
    }

    public static SectionBuilder builder() {
        return new SectionBuilder();
    }

    public static class SectionBuilder {

        private Long id;
        private Long lineId;
        private Long downStationId;
        private Long upStationId;
        private Integer distance;

        public SectionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SectionBuilder lineId(Long lineId) {
            this.lineId = lineId;
            return this;
        }

        public SectionBuilder downStationId(Long downStationId) {
            this.downStationId = downStationId;
            return this;
        }

        public SectionBuilder upStationId(Long upStationId) {
            this.upStationId = upStationId;
            return this;
        }

        public SectionBuilder distance(Integer distance) {
            this.distance = distance;
            return this;
        }

        public Section build() {
            return new Section(id, lineId, downStationId, upStationId, distance);
        }
    }

}
