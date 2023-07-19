package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.domain.Sections;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final StationDao stationDao;

    private RowMapper<Section> rowMapper;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        this.stationDao = stationDao;
        this.rowMapper = (rs, rowNum) ->
                new Section(
                        rs.getLong("id"),
                        stationDao.findById(rs.getLong("up_station_id")),
                        stationDao.findById(rs.getLong("down_station_id")),
                        rs.getInt("distance")
                );
    }

    public Section insert(Section section, Long lineId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("line_id", lineId);
        params.put("distance", section.getDistance());

        Long sectionId = insertAction.executeAndReturnKey(params).longValue();
        return new Section(sectionId, section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public Sections findAllByLineId(Long lineId) {
        String sql = "select * from SECTION where line_id = ?";
        return new Sections(jdbcTemplate.query(sql, rowMapper, lineId));
    }
}
