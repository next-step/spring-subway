package subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.dao.mapper.LineMapper;
import subway.domain.Line;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final LineMapper lineMapper;

    public LineDao(JdbcTemplate jdbcTemplate, DataSource dataSource, LineMapper lineMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
        this.lineMapper = lineMapper;
    }

    public Line insert(Line line) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", line.getId());
        params.put("name", line.getName());
        params.put("color", line.getColor());

        long lineId = insertAction.executeAndReturnKey(params).longValue();
        return new Line(lineId, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "SELECT id, name, color FROM line";
        return jdbcTemplate.query(sql, lineMapper.getRowMapper());
    }

    public Optional<Line> findById(Long id) {
        String sql = "SELECT id, name, color FROM line WHERE id = ?";
        return jdbcTemplate.query(sql, lineMapper.getRowMapper(), id)
            .stream().findAny();
    }

    public void update(Line newLine) {
        String sql = "UPDATE line SET name = ?, color = ? WHERE id = ?";
        jdbcTemplate.update(sql, newLine.getName(), newLine.getColor(), newLine.getId());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM line WHERE id = ?", id);
    }

    public Optional<Line> findByName(final String name) {
        String sql = "SELECT * FROM line WHERE name = ?";
        return jdbcTemplate.query(sql, lineMapper.getRowMapper(), name)
                .stream()
                .findAny();
    }
}
