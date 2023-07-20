package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.StationPair;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private RowMapper<Line> lineRowMapper = (rs, rowNum) ->
            new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
            );

    private RowMapper<StationPair> stationPairRowMapper = (rs, rowNum) ->
            new StationPair(
                    new Station(
                            rs.getLong("s1_id"),
                            rs.getString("s1_name")
                    ),
                    new Station(
                            rs.getLong("s2_id"),
                            rs.getString("s2_name")
                    )
            );

    public LineDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line insert(Line line) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", line.getId());
        params.put("name", line.getName());
        params.put("color", line.getColor());

        Long lineId = insertAction.executeAndReturnKey(params).longValue();
        return new Line(lineId, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "select id, name, color from LINE";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findById(Long id) {
        String sql = "select id, name, color from LINE WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, lineRowMapper, id);
    }

    public void update(Line newLine) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{newLine.getName(), newLine.getColor(), newLine.getId()});
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from Line where id = ?", id);
    }

    public Optional<Line> findByName(final String name) {
        String sql = "select * from LINE WHERE name = ?";
        return jdbcTemplate.query(sql, lineRowMapper, name)
                .stream()
                .findAny();
    }

    public List<StationPair> findAllStationPair(final Long lineId) {
        String sql = "select s1.id as s1_id, s1.name as s1_name, s2.id as s2_id, s2.name as s2_name " +
                "from section " +
                "join station as s1 " +
                "on section.up_station_id = s1.id " +
                "join station as s2 " +
                "on section.down_station_id = s2.id " +
                "where line_id = ?";
        return jdbcTemplate.query(sql, stationPairRowMapper, lineId);
    }
}
