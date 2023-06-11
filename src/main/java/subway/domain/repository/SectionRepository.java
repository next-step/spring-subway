package subway.domain.repository;

import subway.domain.entity.Section;

import java.util.List;

public interface SectionRepository {

    Section insert(Section section);

    List<Section> findAllByLineId(Long lineId);

    List<Section> findAll();

    void deleteByStationId(Long lineId, Long stationId);
}
