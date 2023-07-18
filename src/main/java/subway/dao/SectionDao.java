package subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    private RowMapper<Section> rowMapper = (rs, rowNum) ->
        new Section(
            rs.getLong("id"),
            new Station(
                rs.getLong("up_station_id"),
                rs.getString("us_name")
            ),
            new Station(
                rs.getLong("down_station_id"),
                rs.getString("ds_name")
            ),
            new Line(
                rs.getLong("line_id"),
                rs.getString("l_name"),
                rs.getString("color")
            ),
            rs.getInt("distance")
        );

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
    }

    public Section insert(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("line_id", section.getLine().getId());
        params.put("distance", section.getDistance().getDistance());

        Long sectionId = simpleJdbcInsert.executeAndReturnKey(params).longValue();
        return new Section(sectionId, section.getUpStation(), section.getDownStation(),
            section.getLine(), section.getDistance().getDistance());
    }

    public List<Section> findAll() {
        String sql =
            "select s.id, up_station_id, down_station_id, us.name as us_name, ds.name as ds_name, line_id, l.name as l_name, l.color, distance "
                + "from section s "
                + "inner join line l on (s.line_id = l.id) "
                + "inner join station us on (s.up_station_id = us.id) "
                + "inner join station ds on (s.down_station_id = ds.id)";

        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Section> findById(Long id) {
        String sql =
            "select s.id, up_station_id, down_station_id, us.name as us_name, ds.name as ds_name, line_id, l.name as l_name, l.color, distance "
                + "from section s "
                + "inner join line l on (s.line_id = l.id) "
                + "inner join station us on (s.up_station_id = us.id) "
                + "inner join station ds on (s.down_station_id = ds.id) "
                + "where s.id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void update(Section newSection) {
        String sql = "update section "
            + "set up_station_id = ?, down_station_id = ?, line_id = ?, distance = ? "
            + "where id = ?";
        jdbcTemplate.update(sql, new Object[]{
            newSection.getUpStation().getId(),
            newSection.getDownStation().getId(),
            newSection.getLine().getId(),
            newSection.getDistance().getDistance(),
            newSection.getId()
        });
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from section where id = ?", id);
    }

    public void deleteByDownStationIdAndLineId(Long downStationId, Long lineId) {
        jdbcTemplate.update("delete from section where down_station_id = ? and line_id = ?", downStationId, lineId);
    }

    public Sections findAllByLineId(Long lineId) {
        String sql =
            "select s.id, up_station_id, down_station_id, us.name as us_name, ds.name as ds_name, line_id, l.name as l_name, l.color, distance "
                + "from section s "
                + "inner join line l on (s.line_id = l.id) "
                + "inner join station us on (s.up_station_id = us.id) "
                + "inner join station ds on (s.down_station_id = ds.id) "
                + "where s.line_id = ?";

        return new Sections(jdbcTemplate.query(sql, rowMapper, lineId));
    }
}
