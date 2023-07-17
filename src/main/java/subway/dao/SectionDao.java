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
import subway.domain.Section;
import subway.domain.Station;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private RowMapper<Section> rowMapper = (rs, rowNum) ->
        new Section(
            rs.getLong("id"),
            rs.getObject("upStation", Station.class),
            rs.getObject("downStation", Station.class),
            rs.getObject("line", Line.class),
            rs.getInt("distance")
        );

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
    }

    public Section insert(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("upStation", section.getUpStation());
        params.put("downStation", section.getDownStation());
        params.put("line", section.getLine());
        params.put("distance", section.getDistance());

        Long sectionId = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Section(sectionId, section.getUpStation(), section.getDownStation(),
            section.getLine(), section.getDistance().getDistance());
    }

    public List<Section> findAll() {
        String sql = "select * from section";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Section findById(Long id) {
        String sql = "select * from section WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public void update(Section newSection) {
        String sql = "update section "
            + "set up_station_id = ?, down_station_id = ?, line_id = ?, distance = ? "
            + "where id = ?";
        jdbcTemplate.update(sql, new Object[]{
            newSection.getUpStation().getId(),
            newSection.getDownStation().getId(),
            newSection.getLine().getId(),
            newSection.getDistance().getDistance(),
            newSection.getId()
        });
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from section where id = ?", id);
    }
}
