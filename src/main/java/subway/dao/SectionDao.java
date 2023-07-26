package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
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

    private final RowMapper<Station> rowUpStationMapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("up_station_id"),
                    rs.getString("up_station_name")
            );

    private final RowMapper<Station> rowDownStationMapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("down_station_id"),
                    rs.getString("down_station_name")
            );

    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("section_id"),
                    rowUpStationMapper.mapRow(rs, rowNum),
                    rowDownStationMapper.mapRow(rs, rowNum),
                    rs.getInt("distance")
            );

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public void insert(final Section section, final Long lineId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("line_id", lineId);
        params.put("distance", section.getDistance().getValue());

        insertAction.executeAndReturnKey(params);
    }

    public Sections findAllByLineId(final Long lineId) {
        String sql = "select s.id AS section_id, up_station_id, down_station_id, distance, " +
                "up.name AS up_station_name, down.name AS down_station_name " +
                "from SECTION s join STATION up on s.up_station_id = up.id " +
                "join STATION down on s.down_station_id = down.id " +
                "where line_id = ?";
        return new Sections(jdbcTemplate.query(sql, rowMapper, lineId));
    }

    public void deleteByStation(final Station station, final Long lineId) {
        jdbcTemplate.update("delete from SECTION where (up_station_id = ? or down_station_id = ?) and line_id = ?",
                station.getId(), station.getId(), lineId);
    }

    public void update(final Section newSection, final Long lineId) {
        Long upStationId = newSection.getUpStation().getId();
        Long downStationId = newSection.getDownStation().getId();
        int distance = newSection.getDistance().getValue();
        String sql = "update SECTION " +
                "set up_station_id = ?, down_station_id = ?, line_id = ?, distance = ? " +
                "where id = ?";
        jdbcTemplate.update(sql, new Object[]{upStationId, downStationId, lineId, distance, newSection.getId()});
    }
}
