package subway.dao;

import java.util.List;
import java.util.Optional;
import subway.domain.Section;

public interface SectionDao {

    Section insert(Section section);

    List<Section> findAllByLineId(long lineId);

    boolean existByLineId(Long lineId);

    boolean existByLineIdAndStationId(Long lineId, Long stationId);

    void deleteById(Long id);

    Optional<Section> findByLineIdAndUpStationId(Long lineId, Long upStationId);

    Optional<Section> findByLineIdAndDownStationId(Long lineId, Long downStationId);

    void update(Section section);
}
