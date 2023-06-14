package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private StationDao stationDao;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        this.stationDao = stationDao;
    }

    private final RowMapper<Section> rowMapper =
            (rs, rowNum) -> new Section(
                    rs.getLong("id"),
                    stationDao.findById(rs.getLong("up_station_id")).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역 입니다.")),
                    stationDao.findById(rs.getLong("down_station_id")).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역 입니다.")),
                    rs.getInt("distance")
            );

    public Section insert(Long lineId, Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("line_id", lineId);
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Section(id, section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "select * from section where line_id = ?";
        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

}
