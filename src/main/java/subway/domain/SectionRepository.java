package subway.domain;

public interface SectionRepository {

    Section save(Section section);

    Sections findAllByLineId(long lineId);

    void deleteByLineIdAndDownStationId(long lineId, long stationId);
}
