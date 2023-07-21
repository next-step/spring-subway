package subway.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Line> lineRowMapper;
    private final SectionDao sectionDao;

    public LineDao(JdbcTemplate jdbcTemplate, DataSource dataSource, RowMapper<Line> lineRowMapper,
            SectionDao sectionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
        this.lineRowMapper = lineRowMapper;
        this.sectionDao = sectionDao;
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
        String sql = "SELECT * FROM LINE";
        List<Line> linesWithOutSections = jdbcTemplate.query(sql, lineRowMapper);
        List<Line> linesWithSections = new ArrayList<>();
        for (Line line : linesWithOutSections) {
            linesWithSections.add(
                    new Line(line.getId(), line.getName(), line.getColor(), sectionDao.findAllByLineId(line.getId())));
        }
        return linesWithSections;
    }

    public Optional<Line> findById(Long id) {
        String sql = "SELECT * FROM LINE AS L WHERE L.id = ?";
        Line line = jdbcTemplate.queryForObject(sql, lineRowMapper, id);
        if (line == null) {
            return Optional.empty();
        }
        return Optional.of(
                new Line(line.getId(), line.getName(), line.getColor(), sectionDao.findAllByLineId(line.getId())));
    }

    public void update(Line newLine) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, newLine.getName(), newLine.getColor(), newLine.getId());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from Line where id = ?", id);
    }
}
