package subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.dao.mapper.SectionMapper;
import subway.domain.Section;

@Repository
public class SectionDao {

    private static final String SECTION_FIELD_SQL = "s.id AS id, s.up_station_id AS up_station_id, s.down_station_id AS down_station_id, s.distance AS distance";
    private static final String LINE_FIELD_SQL = "l.id AS line_id, l.name AS line_name, l.color AS line_color";
    private static final String UP_STATION_FIELD_SQL = "up.id AS up_station_id, up.name as up_station_name";
    private static final String DOWN_STATION_FIELD_SQL = "down.id AS down_station_id, down.name AS down_station_name";
    private static final String SECTION_FIELD_SELECT_SQL = "SELECT " + SECTION_FIELD_SQL +", " + LINE_FIELD_SQL + ", " + UP_STATION_FIELD_SQL + ", " + DOWN_STATION_FIELD_SQL + " ";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final SectionMapper sectionMapper;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource,
        SectionMapper sectionMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
        this.sectionMapper = sectionMapper;
    }

    public Section insert(final Section section) {
        // TODO: 지저분하다. rowMapper 를 활용하는 방법은 없나 ? 직접 타이핑하면 틀릴 수도 있고 귀찮음.
        SqlParameterSource params = new MapSqlParameterSource()
            .addValue("id", section.getId())
            .addValue("line_id", section.getLine().getId())
            .addValue("up_station_id", section.getUpStation().getId())
            .addValue("down_station_id", section.getDownStation().getId())
            .addValue("distance", section.getDistance());
        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Section(id, section.getLine(), section.getUpStation(),
            section.getDownStation(), section.getDistance());
    }

    public List<Section> findAll() {
        final String sql = SECTION_FIELD_SELECT_SQL
            + "FROM section AS s "
            + "JOIN line AS l "
            + "ON s.line_id = l.id "
            + "JOIN station AS up "
            + "ON s.up_station_id = up.id "
            + "JOIN station AS down "
            + "ON s.down_station_id = down.id ";
        return jdbcTemplate.query(sql, sectionMapper.getRowMapper());
    }

    public List<Section> findAllByLineId(final long lineId) {
        final String sql = SECTION_FIELD_SELECT_SQL
            + "FROM section AS s "
            + "JOIN line AS l "
            + "ON s.line_id = l.id "
            + "JOIN station AS up "
            + "ON s.up_station_id = up.id "
            + "JOIN station AS down "
            + "ON s.down_station_id = down.id "
            + "WHERE l.id = ?";
        return jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId);
    }

    public boolean existByLineIdAndStationId(final long lineId, final long stationId) {
        final String sql = SECTION_FIELD_SELECT_SQL
            + "FROM section AS s "
            + "JOIN line AS l "
            + "ON s.line_id = l.id "
            + "JOIN station AS up "
            + "ON s.up_station_id = up.id "
            + "JOIN station AS down "
            + "ON s.down_station_id = down.id "
            + "WHERE l.id = ? AND (s.up_station_id = ? OR s.down_station_id = ?)";
        return !jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId, stationId, stationId)
            .isEmpty();
    }

    public long count(final long lineId) {
        final String sql = SECTION_FIELD_SELECT_SQL
            + "FROM section AS s "
            + "JOIN line AS l "
            + "ON s.line_id = l.id "
            + "JOIN station AS up "
            + "ON s.up_station_id = up.id "
            + "JOIN station AS down "
            + "ON s.down_station_id = down.id "
            + "WHERE l.id = ?";
        return jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId)
            .size();
    }

    public void update(final Section newSection) {
        final String sql = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(
            sql,
            newSection.getUpStation().getId(),
            newSection.getDownStation().getId(),
            newSection.getDistance(),
            newSection.getId()
        );
    }

    public void delete(final long id) {
        final String sql = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

}
