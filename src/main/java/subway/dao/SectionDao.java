package subway.dao;

import java.util.List;
import java.util.Optional;
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

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final SectionMapper sectionMapper;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource, SectionMapper sectionMapper) {
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
            .addValue("up_station_id", section.getUpStationId())
            .addValue("down_station_id", section.getDownStationId())
            .addValue("distance", section.getDistance());
        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Section(id, section.getLine(), section.getUpStationId(),
            section.getDownStationId(), section.getDistance());
    }

    public Optional<Section> findLastSection(final Long lineId) {
        String sql = "SELECT * FROM section S1 " +
            "WHERE S1.line_id = ? " +
            "AND NOT EXISTS(SELECT * FROM section S2 WHERE S1.down_station_id = S2.up_station_id)";
        return jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId)
            .stream()
            .findAny();
    }

    public List<Section> findAll(final long lineId) {
        final String sql = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId);
    }

    public void update(final Section newSection) {
        final String sql = "UPDATE section SET up_station_id = ?, down_station_id = ?, distance = ? WHERE id = ?";
        jdbcTemplate.update(
            sql,
            newSection.getUpStationId(),
            newSection.getDownStationId(),
            newSection.getDistance(),
            newSection.getId()
        );
    }

    public void deleteLastSection(final long lineId, final long stationId) {
        final String sql = "DELETE FROM section WHERE line_id = ? AND down_station_id = ?";
        jdbcTemplate.update(sql, lineId, stationId);
    }

    public void delete(final long id) {
        final String sql = "DELETE FROM section WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public long count(final long lineId) {
        final String sql = "SELECT * FROM section WHERE line_id = ?";
        return jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId)
            .size();
    }

    public boolean existByLineIdAndStationId(final long lineId, final long stationId) {
        final String sql = "SELECT * FROM section WHERE line_id = ? AND (up_station_id = ? OR down_station_id = ?)";
        return !jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId, stationId, stationId).isEmpty();
    }
}
