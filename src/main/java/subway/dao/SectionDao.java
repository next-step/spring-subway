package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getLong("distance"),
                    rs.getObject("next_section_id", Long.class),
                    rs.getObject("prev_section_id", Long.class)
            );

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section insert(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("line_id", section.getLineId());
        params.put("up_station_id", section.getUpStationId());
        params.put("down_station_id", section.getDownStationId());
        params.put("distance", section.getDistance());
        params.put("next_section_id", section.getNextSectionId());
        params.put("prev_section_id", section.getPrevSectionId());

        Long sectionId = insertAction.executeAndReturnKey(params).longValue();
        return new Section(
                sectionId,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance(),
                section.getNextSectionId(),
                section.getPrevSectionId()
        );
    }

    public Section findById(Long id) {
        String sql = "select * from SECTION where id = ?";

        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }
}
