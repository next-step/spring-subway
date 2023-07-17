package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong(1),
                    new Station(rs.getLong(2), rs.getString(3)),
                    new Station(rs.getLong(4), rs.getString(5)),
                    new Line(rs.getLong(6), rs.getString(7), rs.getString(8)),
                    rs.getInt(9)
            );

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
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

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "select section.id, upward.id, upward.name, downward.id, downward.name, line.id, line.name, line.color, section.distance " +
                "from SECTION section " +
                "left join LINE line on section.line_id=line.id " +
                "left join STATION upward on section.upward_id = upward.id " +
                "left join STATION downward on section.downward_id = downward.id " +
                "where section.line_id = ?";

        return jdbcTemplate.query(sql, rowMapper, lineId);
    }
}
