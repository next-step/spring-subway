package subway.infrastrucure;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;

@Repository
public class LineDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public LineDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line insert(Line line) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(line);
        Long lineId = jdbcInsert.executeAndReturnKey(param).longValue();
        return new Line(lineId, line.getName(), line.getColor());
    }

    public List<Line> findAll() {
        String sql = "SELECT id, name, color FROM line";
        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Line findById(Long id) {
        String sql = "SELECT id, name, color FROM LINE WHERE id = :id";
        return jdbcTemplate.queryForObject(sql, Map.of("id", id), lineRowMapper);
    }

    public void update(Line line) {
        String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";
        SqlParameterSource param = new BeanPropertySqlParameterSource(line);
        jdbcTemplate.update(sql, param);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM Line WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    private final RowMapper<Line> lineRowMapper = (rs, rowNum) ->
        new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color"));
}
