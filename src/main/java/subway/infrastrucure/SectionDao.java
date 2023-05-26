package subway.infrastrucure;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@Repository
public class SectionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
    }

    public Section insert(Section section) {
        long savedId = jdbcInsert.executeAndReturnKey(new BeanPropertySqlParameterSource(section))
            .longValue();
        section.injectionId(savedId);
        return section;
    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "SELECT se.id, l.id AS line_id, l.name AS line_name, l.color AS line_color, "
                            + "u.id AS up_station_id, u.name AS up_station_name, "
                            + "d.id AS down_station_id, d.name AS down_station_name, se.distance "
                    + "FROM section se "
                    + "INNER JOIN line l ON l.id = se.line_id "
                    + "INNER JOIN station u ON u.id = se.up_station_id "
                    + "INNER JOIN station d ON d.id = se.down_station_id "
                    + "WHERE se.line_id = :lineId";
        return jdbcTemplate.query(sql, Map.of("lineId", lineId), sectionRowMapper);
    }

    public List<Section> findAll() {
        String sql = "SELECT se.id, l.id AS line_id, l.name AS line_name, l.color AS line_color, "
                            + "u.id AS up_station_id, u.name AS up_station_name, "
                            + "d.id AS down_station_id, d.name AS down_station_name, se.distance "
                    + "FROM section se "
                    + "INNER JOIN line l ON l.id = se.line_id "
                    + "INNER JOIN station u ON u.id = se.up_station_id "
                    + "INNER JOIN station d ON d.id = se.down_station_id ";
        return jdbcTemplate.query(sql, sectionRowMapper);
    }

    public void delete(Long lineId, Long stationId) {
        String sql = "DELETE FROM section WHERE line_id = :lineId AND down_station_id = :stationId";
        jdbcTemplate.update(sql, Map.of("lineId", lineId, "stationId", stationId));
    }

    private final RowMapper<Section> sectionRowMapper = (rs, rowNum) ->
        new Section(
            rs.getLong("id"),
            new Line(rs.getLong("line_id"), rs.getString("line_name"), rs.getString("line_color")),
            new Station(rs.getLong("up_station_id"), rs.getString("up_station_name")),
            new Station(rs.getLong("down_station_id"), rs.getString("down_station_name")),
            rs.getInt("distance"));
}
