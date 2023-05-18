package subway.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
    }

    public Section insert(Section section) {
        long savedId = jdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(section))
            .longValue();
        section.injectionId(savedId);
        return section;
    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = :lineId";
        return jdbcTemplate.query(sql, Map.of("lineId", lineId), sectionRowMapper);
    }

    public void delete(Long lineId, Long stationId) {
        String sql = "DELETE FROM section WHERE line_id = :lineId AND down_station_id = :stationId";
        jdbcTemplate.update(sql, Map.of("lineId", lineId, "stationId", stationId));
    }

    private final RowMapper<Section> sectionRowMapper = (rs, rowNum) ->
        new Section(
            rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance"));
}
