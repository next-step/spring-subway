package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.SectionGroup;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("section_id"),
                    new Station(rs.getLong("upward_id"), rs.getString("upward_name")),
                    new Station(rs.getLong("downward_id"), rs.getString("downward_name")),
                    new Line(rs.getLong("line_id"), rs.getString("line_name"), rs.getString("line_Color")),
                    rs.getInt("section_distance")
            );

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section insert(final Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("upward_id", section.getUpward().getId());
        params.put("downward_id", section.getDownward().getId());
        params.put("line_id", section.getLine().getId());
        params.put("distance", section.getDistance());

        Long sectionId = insertAction.executeAndReturnKey(params).longValue();
        return new Section(sectionId, section.getUpward(), section.getDownward(), section.getLine(), section.getDistance());
    }

    public SectionGroup findAllByLineId(final Long lineId) {
        String sql = "select section.id as section_id, " +
                "upward.id as upward_id, " +
                "upward.name as upward_name, " +
                "downward.id as downward_id, " +
                "downward.name as downward_name, " +
                "line.id as line_id, " +
                "line.name as line_name, " +
                "line.color as line_color," +
                "section.distance as section_distance " +
                "from SECTION section " +
                "left join LINE line on section.line_id=line.id " +
                "left join STATION upward on section.upward_id = upward.id " +
                "left join STATION downward on section.downward_id = downward.id " +
                "where section.line_id = ?";

        return new SectionGroup(jdbcTemplate.query(sql, rowMapper, lineId));
    }
}
