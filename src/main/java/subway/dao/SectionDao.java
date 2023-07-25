package subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;

@Repository
public class SectionDao {

    private static final String FIND_ALL_BY_LINE_ID_SQL =
            "SELECT S.*, US.id AS US_ID, US.name AS US_NAME, DS.id AS DS_ID, DS.name AS DS_NAME FROM SECTIONS AS S "
                    + "JOIN STATION AS US ON S.line_id = ? AND S.up_station_id = US.id "
                    + "JOIN STATION AS DS ON S.line_id = ? AND S.down_station_id = DS.id";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Section> sectionRowMapper;

    SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource, RowMapper<Section> sectionRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTIONS")
                .usingGeneratedKeyColumns("id");
        this.sectionRowMapper = sectionRowMapper;
    }

    public Section insert(long lineId, Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("line_id", lineId);
        params.put("up_section_id", extractUpSectionId(section));
        params.put("down_section_id", extractDownSectionId(section));
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        long id = insertAction.executeAndReturnKey(params).longValue();
        return buildSection(id, section);
    }

    private Long extractUpSectionId(Section section) {
        if (section.getUpSection() != null) {
            return section.getUpSection().getId();
        }
        return null;
    }

    private Long extractDownSectionId(Section section) {
        if (section.getDownSection() != null) {
            return section.getDownSection().getId();
        }
        return null;
    }

    List<Section> findAllByLineId(long lineId) {
        return jdbcTemplate.query(FIND_ALL_BY_LINE_ID_SQL, sectionRowMapper, lineId, lineId);
    }

    private Section buildSection(long sectionId, Section section) {
        return Section.builder()
                .id(sectionId)
                .upStation(section.getUpStation())
                .downStation(section.getDownStation())
                .upSection(section.getUpSection())
                .downSection(section.getDownSection())
                .distance(section.getDistance())
                .build();
    }

    public void update(Section section) {
        String sql = "UPDATE SECTIONS SET (up_station_id, down_station_id, distance) = (?, ?, ?) WHERE id = ?";
        jdbcTemplate.update(sql, section.getUpStation().getId(), section.getDownStation().getId(),
                section.getDistance(), section.getId());
    }

    public void deleteBySectionId(long sectionId) {
        String deleteSql = "DELETE FROM SECTIONS AS S WHERE S.id = ?";
        jdbcTemplate.update(deleteSql, sectionId);
    }
}
