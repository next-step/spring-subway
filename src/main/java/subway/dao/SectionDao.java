package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SectionDao {

    private static final String FIND_ALL_BY_LINE_ID_SQL =
            "SELECT S.*," +
                    "(SELECT US.id FROM STATION AS US WHERE S.up_station_id = US.id) AS US_ID," +
                    "(SELECT US.name FROM STATION AS US WHERE S.up_station_id = US.id) AS US_NAME," +
                    "(SELECT DS.id FROM STATION AS DS WHERE S.down_station_id = DS.id) AS DS_ID," +
                    "(SELECT DS.name FROM STATION AS DS WHERE S.down_station_id = DS.id) AS DS_NAME " +
                    "FROM SECTIONS AS S " +
                    "where line_id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT S.*," +
                    "(SELECT US.id FROM STATION AS US WHERE S.up_station_id = US.id) AS US_ID," +
                    "(SELECT US.name FROM STATION AS US WHERE S.up_station_id = US.id) AS US_NAME," +
                    "(SELECT DS.id FROM STATION AS DS WHERE S.down_station_id = DS.id) AS DS_ID," +
                    "(SELECT DS.name FROM STATION AS DS WHERE S.down_station_id = DS.id) AS DS_NAME " +
                    "FROM SECTIONS AS S ";

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

    public Section insert(Section section, Long lineId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("line_id", lineId);
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        Long id = insertAction.executeAndReturnKey(params).longValue();
        return buildSection(id, section);
    }

    public List<Section> findAllByLineId(Long lineId) {
        return jdbcTemplate.query(FIND_ALL_BY_LINE_ID_SQL, sectionRowMapper, lineId);
    }

    private Section buildSection(Long sectionId, Section section) {
        return Section.builder()
                .id(sectionId)
                .upStation(section.getUpStation())
                .downStation(section.getDownStation())
                .upSection(section.getUpSection())
                .downSection(section.getDownSection())
                .distance(section.getDistance())
                .build();
    }

    public void deleteByLineIdAndStationId(Long lineId, Long stationId) {
        String deleteSql = "delete from SECTIONS where line_id = ? and down_station_id = ? or up_station_id = ?";
        jdbcTemplate.update(deleteSql, lineId, stationId, stationId);
    }

    public void update(Section section) {
        String sql = "update SECTIONS set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, section.getUpStation().getId(), section.getDownStation().getId(),
                section.getDistance(), section.getId());
    }

    public List<Section> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, sectionRowMapper);
    }
}
