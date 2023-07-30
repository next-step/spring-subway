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

    private static final String SELECT_ALL_FROM_SECTION_QUERY
            = "SELECT s.*, " +
            "up_station.name AS up_station_name, down_station.name AS down_station_name, " +
            "line.id AS line_id, line.name AS line_name, line.color AS line_color " +
            "FROM section s " +
            "JOIN station AS up_station " +
            "ON s.up_station_id = up_station.id " +
            "JOIN station AS down_station " +
            "ON s.down_station_id = down_station.id " +
            "JOIN line " +
            "ON s.line_id = line.id ";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Section> rowMapper;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource, final RowMapper<Section> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        this.rowMapper = rowMapper;
    }

    public Section insert(final Section section) {
        final Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("line_id", section.getLine().getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Section(id, section.getLine(), section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    public long count(final long lineId) {
        String sql = SELECT_ALL_FROM_SECTION_QUERY + "WHERE line_id = ?";
        return jdbcTemplate.query(sql, rowMapper, lineId)
                .size();
    }

    public List<Section> findAll(final long lineId) {
        String sql = SELECT_ALL_FROM_SECTION_QUERY + "WHERE line_id = ?";
        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public void update(final Section newSection) {
        String sql = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(
                sql,
                newSection.getUpStation().getId(),
                newSection.getDownStation().getId(),
                newSection.getDistance(),
                newSection.getId()
        );
    }

    public void delete(final Section section) {
        String sql = "DELETE FROM section WHERE line_id = ? AND down_station_id = ?";
        jdbcTemplate.update(sql, section.getLine().getId(), section.getDownStation().getId());
    }
}
