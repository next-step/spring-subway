package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Line> rowMapper;
    private final SectionDao sectionDao;

    public LineDao(JdbcTemplate jdbcTemplate, DataSource dataSource, RowMapper<Line> rowMapper, SectionDao sectionDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
        this.rowMapper = rowMapper;
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
        String sql = "select * from LINE";
        List<Line> lines = jdbcTemplate.query(sql, rowMapper);

        return lines.stream()
                .map(line -> setSections(line))
                .collect(Collectors.toList());
    }

    public Optional<Line> findById(Long id) {
        String sql = "select * from LINE WHERE id = ?";
        Optional<Line> lineOptional = Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));

        lineOptional.ifPresent(line -> setSections(line));

        return lineOptional;
    }

    public Optional<Line> findByName(String name) {
        String sql = "select * from LINE WHERE name = ?";
        Optional<Line> lineOptional = jdbcTemplate.query(sql, rowMapper, name)
                .stream()
                .findAny();

        lineOptional.ifPresent(line -> setSections(line));

        return lineOptional;
    }

    private Line setSections(Line line) {
        return new Line(line.getId(), line.getName(), line.getColor(), findSectionsByLineId(line.getId()));
    }

    private List<Section> findSectionsByLineId(Long longId) {
        return sectionDao.findAllByLineId(longId);
    }

    public void update(Line newLine) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        jdbcTemplate.update(sql, newLine.getName(), newLine.getColor(), newLine.getId());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from Line where id = ?", id);
    }
}
