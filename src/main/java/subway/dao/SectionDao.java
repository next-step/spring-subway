package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Line> rowMapper = (rs, rowNum) ->
            new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
            );

    public SectionDao(JdbcTemplate jdbcTemplate, SimpleJdbcInsert insertAction) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = insertAction;
    }

    public Section insert(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("upward_id", section.getUpward().getId());
        params.put("downward_id", section.getDownward().getId());
        params.put("line_id", section.getLine().getId());
        params.put("distance", section.getDistance());

        Long sectionId = insertAction.executeAndReturnKey(params).longValue();
        return new Section(sectionId, section.getUpward(), section.getDownward(), section.getLine(), section.getDistance());
    }
}
