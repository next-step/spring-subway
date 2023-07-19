package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
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
                    new Station(rs.getLong("up_station_id"), rs.getString("up_station_name")),
                    new Station(rs.getLong("down_station_id"), rs.getString("down_station_name")),
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
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("line_id", section.getLine().getId());
        params.put("distance", section.getDistance());

        Long sectionId = insertAction.executeAndReturnKey(params).longValue();
        return new Section(sectionId, section.getUpStation(), section.getDownStation(), section.getLine(), section.getDistance());
    }

    public Sections findAllByLineId(final Long lineId) {
        String sql = "select section.id as section_id, " +
                "up_station.id as up_station_id, " +
                "up_station.name as up_station_name, " +
                "down_station.id as down_station_id, " +
                "down_station.name as down_station_name, " +
                "line.id as line_id, " +
                "line.name as line_name, " +
                "line.color as line_color," +
                "section.distance as section_distance " +
                "from SECTION section " +
                "left join LINE line on section.line_id=line.id " +
                "left join STATION up_station on section.up_station_id = up_station.id " +
                "left join STATION down_station on section.down_station_id = down_station.id " +
                "where section.line_id = ?";

        return new Sections(jdbcTemplate.query(sql, rowMapper, lineId));
    }

    public void deleteByLineIdAndStationId(Long lineId, Long stationId) {
        String sql = "delete from SECTION where line_id = ? and down_station_id = ?";
        jdbcTemplate.update(sql, lineId, stationId);
    }
}
