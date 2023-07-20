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
            "select S.*, L.*, US.id as US_ID, US.name as US_NAME, DS.id as DS_ID, DS.name as DS_NAME from SECTIONS as S "
                    + "JOIN LINE as L ON S.line_id = ? AND S.line_id = L.id "
                    + "JOIN STATION as US ON S.up_station_id = US.id "
                    + "JOIN STATION as DS ON S.down_station_id = DS.id";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Section> sectionRowMapper;

    SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource, RowMapper<Section> sectionRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("sections")
                .usingGeneratedKeyColumns("id");
        this.sectionRowMapper = sectionRowMapper;
    }

    public Section insert(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("line_id", section.getLine().getId());
        params.put("up_section_id", extractUpSectionId(section));
        params.put("down_section_id", extractDownSectionId(section));
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        Long id = insertAction.executeAndReturnKey(params).longValue();
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

    public List<Section> findAllByLineId(Long lineId) {
        return jdbcTemplate.query(FIND_ALL_BY_LINE_ID_SQL, sectionRowMapper, lineId);
    }

    private Section buildSection(Long sectionId, Section section) {
        return Section.builder()
                .id(sectionId)
                .line(section.getLine())
                .upStation(section.getUpStation())
                .downStation(section.getDownStation())
                .upSection(section.getUpSection())
                .downSection(section.getDownSection())
                .distance(section.getDistance())
                .build();
    }

    public void deleteByLineIdAndDownStationId(Long lineId, Long stationId) {
        String deleteSql = "delete from SECTIONS as S where S.line_id = ? AND S.down_station_id = ?";
        jdbcTemplate.update(deleteSql, lineId, stationId);
    }

    public void update(Section section) {
        String sql = "update SECTIONS set (up_station_id, down_station_id, distance) = (?, ?, ?) where id = ?";
        jdbcTemplate.update(sql, section.getUpStation().getId(), section.getDownStation().getId(),
                section.getDistance(), section.getId());
    }
}
