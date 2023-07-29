package subway.dao;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.exception.SubwayDataAccessException;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class LineDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Line> lineRowMapper = (rs, rowNum) ->
            new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
            );

    public LineDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("LINE")
                .usingGeneratedKeyColumns("id");
    }

    public Line insert(final Line line) {
        try {
            final Long lineId = insertAction.executeAndReturnKey(getLineParams(line)).longValue();

            return new Line(lineId, line.getName(), line.getColor());
        } catch (DuplicateKeyException e) {
            throw new SubwayDataAccessException("이미 존재하는 노선 이름입니다. 입력한 이름: " + line.getName(), e);
        }
    }

    public List<Line> findAll() {
        final String sql = "select id, name, color from LINE";

        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Optional<Line> findById(Long id) {
        try {
            final String sql = "select id, name, color from LINE WHERE id = ?";

            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, lineRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void update(final Line newLine) {
        String sql = "update LINE set name = ?, color = ? where id = ?";

        final int affectedRowCount = jdbcTemplate.update(sql, newLine.getName(), newLine.getColor(), newLine.getId());
        if (affectedRowCount == 0) {
            throw new SubwayDataAccessException("노선이 존재하지 않습니다. 입력한 식별자: " + newLine.getId());
        }
    }

    public void deleteById(final Long id) {
        final int affectedRowCount = jdbcTemplate.update("delete from Line where id = ?", id);
        if (affectedRowCount == 0) {
            throw new SubwayDataAccessException("노선이 존재하지 않습니다. 입력한 식별자: " + id);
        }
    }

    private Map<String, Object> getLineParams(final Line line) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", line.getId());
        params.put("name", line.getName());
        params.put("color", line.getColor());
        return params;
    }
}
