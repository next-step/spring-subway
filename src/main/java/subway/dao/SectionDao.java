package subway.dao;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.domain.Sections;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Section> rowMapper;
    private final RowMapper<Long> idRowMapper;

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource, final StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        this.rowMapper = (rs, rowNum) ->
                new Section(
                        rs.getLong("id"),
                        stationDao.findById(rs.getLong("up_station_id")),
                        stationDao.findById(rs.getLong("down_station_id")),
                        rs.getInt("distance")
                );
        this.idRowMapper = (rs, rowNum) -> rs.getLong("id");
    }

    public void insert(final Section section, final Long lineId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("line_id", lineId);
        params.put("distance", section.getDistance().getValue());

        insertAction.executeAndReturnKey(params);
    }

    public Long findIdByStationIdsAndLineId(final Long upStationId, final Long downStationId, final Long lineId) {
        String sql = "select id from SECTION where up_station_id = ? and down_station_id = ? and line_id = ?";

        return jdbcTemplate.queryForObject(sql, idRowMapper, upStationId, downStationId, lineId);
    }

    public Sections findAllByLineId(final Long lineId) {
        String sql = "select * from SECTION where line_id = ?";

        return new Sections(jdbcTemplate.query(sql, rowMapper, lineId));
    }

    public void deleteById(final Long id) {
        String sql = "delete from SECTION where id = ?";

        jdbcTemplate.update(sql, id);
    }
}
