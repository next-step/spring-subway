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
    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT id, line_id, up_station_id, down_station_id, distance FROM section WHERE line_id = :lineId";
        return jdbcTemplate.query(sql, Map.of("lineId", lineId), sectionRowMapper);
    }

    public Section save(Section section) {
        long savedId = simpleJdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(section))
            .longValue();
        section.injectionId(savedId);
        return section;
    }

    private final RowMapper<Section> sectionRowMapper = (rs, rowNum) ->
        new Section(rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance"));
}
