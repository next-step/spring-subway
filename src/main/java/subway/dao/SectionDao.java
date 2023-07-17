package subway.dao;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("sections")
                .usingGeneratedKeyColumns("id");
    }

    public Section insert(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        Long upSectionId = null;
        if (section.getUpSection() != null) {
            upSectionId = section.getUpSection().getId();
        }
        Long downSectionId = null;
        if (section.getDownSection() != null) {
            downSectionId = section.getDownSection().getId();
        }
        params.put("up_section_id", upSectionId);
        params.put("down_section_id", downSectionId);
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());

        Long sectionId = insertAction.executeAndReturnKey(params).longValue();
        return Section.builder()
                .id(sectionId)
                .upStation(section.getUpStation())
                .downStation(section.getDownStation())
                .upSection(section.getUpSection())
                .downSection(section.getDownSection())
                .build();
    }
}
