package subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Distance;
import subway.domain.Section;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class SectionDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("section_id"),
                    new Station(rs.getLong("up_station_id"), rs.getString("up_station_name")),
                    new Station(rs.getLong("down_station_id"), rs.getString("down_station_name")),
                    new Distance(rs.getInt("section_distance"))
            );

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Optional<Section> findById(Long id) {
        String sql = "select section.id as section_id, " +
                "up_station.id as up_station_id, " +
                "up_station.name as up_station_name, " +
                "down_station.id as down_station_id, " +
                "down_station.name as down_station_name, " +
                "section.distance as section_distance " +
                "from SECTION section " +
                "left join STATION up_station on section.up_station_id = up_station.id " +
                "left join STATION down_station on section.down_station_id = down_station.id " +
                "where section.id = ?";
        try {
            Section section = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.of(section);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Section insert(final Section section, final Long lineId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("line_id", lineId);
        params.put("distance", section.getDistance());

        Long sectionId = insertAction.executeAndReturnKey(params).longValue();
        return new Section(sectionId, section.getUpStation(), section.getDownStation(), new Distance(section.getDistance()));
    }

    public void delete(final Section section) {
        String sql = "delete from SECTION where id= ?";
        jdbcTemplate.update(sql, section.getId());
    }
}
