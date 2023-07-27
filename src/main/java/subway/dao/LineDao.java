package subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.LineName;

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
        Map<String, Object> params = new HashMap<>();
        params.put("id", line.getId());
        params.put("name", line.getLineName().getValue());
        params.put("color", line.getColor().getValue());

        Long lineId = insertAction.executeAndReturnKey(params).longValue();

        return new Line(lineId, line.getLineName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "select id, name, color from LINE";

        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Line> findById(final Long id) {
        String sql = "select id, name, color from LINE WHERE id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean exists(final LineName lineName) {
        String sql = "select exists(select id from LINE where name = ?)";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, lineName.getValue()));
    }

    public void update(final Line newLine) {
        String sql = "update LINE set name = ?, color = ? where id = ?";

        jdbcTemplate.update(sql, newLine.getLineName().getValue(), newLine.getColor().getValue(), newLine.getId());
    }

    public void deleteById(final Long id) {
        String sql = "delete from Line where id = ?";

        jdbcTemplate.update(sql, id);
    }
}
