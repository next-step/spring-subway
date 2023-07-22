package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.LineWithStations;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Line findById(final Long id) {
        final String sql = "select id, name, color from LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public List<LineWithStations> findAllById(final Long id) {
        final String sql = "select l.id as line_id,  l.name as line_name, l.color as line_color, " +
                "s.up_station_id as section_up_station_id, s.down_station_id as section_down_station_id, "
                +
                "UP_STATION.name as section_up_station_name, DOWN_STATION.name as section_down_station_name "
                + "from SECTION s "
                + "left join LINE l on s.line_id = l.id "
                + "left join STATION UP_STATION on s.up_station_id = UP_STATION.id "
                + "left join STATION DOWN_STATION on s.down_station_id = DOWN_STATION.id "
                + "where l.id = ?";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new LineWithStations(
                        new Line(
                                rs.getLong("line_id"),
                                rs.getString("line_name"),
                                rs.getString("line_color")
                        ),
                        new Station(
                                rs.getLong("section_up_station_id"),
                                rs.getString("section_up_station_name")
                        ),
                        new Station(
                                rs.getLong("section_down_station_id"),
                                rs.getString("section_down_station_name")
                        )
                ), id);
    }

    public void update(final Line newLine) {
        final String sql = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql,
                new Object[]{newLine.getName(), newLine.getColor(), newLine.getId()});
    }

    public void deleteById(final Long id) {
        jdbcTemplate.update("delete from Line where id = ?", id);
    }
}
