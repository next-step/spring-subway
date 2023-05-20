package subway.domain.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import subway.domain.entity.Section;

import javax.sql.DataSource;
import java.util.List;

public interface SectionRepository {

    Section insert(Section section);

    List<Section> findAllByLineId(Long lineId);

    void deleteByStationId(Long lineId, Long stationId);
}
