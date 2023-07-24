package subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.LineWithSection;
import subway.domain.Section;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Line> rowMapper = (rs, rowNum) ->
            new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
            );

    public LineDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line insert(final Line line) {
        final Map<String, Object> params = new HashMap<>();
        params.put("id", line.getId());
        params.put("name", line.getName());
        params.put("color", line.getColor());

        final Long lineId = insertAction.executeAndReturnKey(params).longValue();
        return new Line(lineId, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        final String sql = "select id, name, color from LINE";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<LineWithSection> findAllById(final Long id) {
        final String sql = "select l.id as line_id,  l.name as line_name, l.color as line_color, " +
                "s.id as section_id, s.up_station_id as up_station_id, s.down_station_id as down_station_id, s.distance as distance "
                + "from SECTION s "
                + "left join LINE l on s.line_id = l.id "
                + "where l.id = ?";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new LineWithSection(
                        new Line(
                                rs.getLong("line_id"),
                                rs.getString("line_name"),
                                rs.getString("line_color")
                        ),
                        new Section(
                                rs.getLong("section_id"),
                                rs.getLong("line_id"),
                                rs.getLong("up_station_id"),
                                rs.getLong("down_station_id"),
                                rs.getLong("distance")
                        )
                ), id);
    }

    public void update(final Line newLine) {
        final String sql = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{newLine.getName(), newLine.getColor(), newLine.getId()});
    }

    public void deleteById(final Long id) {
        jdbcTemplate.update("delete from Line where id = ?", id);
    }
}
