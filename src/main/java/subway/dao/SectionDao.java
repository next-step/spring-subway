package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Section> rowMapper;

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
    }

    public void insert(final Section section, final Long lineId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("line_id", lineId);
        params.put("distance", section.getDistance());

        insertAction.executeAndReturnKey(params);
    }

    public Sections findAllByLineId(final Long lineId) {
        String sql = "select * from SECTION where line_id = ?";
        return new Sections(jdbcTemplate.query(sql, rowMapper, lineId));
    }

    public void deleteByStation(final Station station, final Long lineId) {
        jdbcTemplate.update("delete from SECTION where down_station_id = ? and line_id = ?",
                station.getId(), lineId);
    }

    public void deleteById(final Long id) {
        String sql = "delete from SECTION where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
