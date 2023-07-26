package subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
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
        SqlParameterSource params = new BeanPropertySqlParameterSource(section);
        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Section(id, section.getLineId(), section.getUpStationId(),
            section.getDownStationId(), section.getDistance());
    }

    public Optional<Section> findLastSection(final Long lineId) {
        String sql = "select * from SECTION S1 " +
            "where S1.line_id = ? " +
            "and not exists(select * from section S2 where S1.down_station_id = S2.up_station_id)";
        return jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId)
            .stream()
            .findAny();
    }

    public List<Section> findAll(final long lineId) {
        String sql = "select * from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId);
    }

    public void update(final Section newSection) {
        String sql = "update SECTION set up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(
            sql,
            newSection.getUpStationId(),
            newSection.getDownStationId(),
            newSection.getDistance(),
            newSection.getId()
        );
    }

    public void deleteLastSection(final long lineId, final long stationId) {
        String sql = "delete from SECTION where line_id = ? and down_station_id = ?";
        jdbcTemplate.update(sql, lineId, stationId);
    }

    public void delete(long id) {
        String sql = "delete from SECTION where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public long count(final long lineId) {
        String sql = "select * from section where line_id = ?";
        return jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId)
            .size();
    }

    public boolean existByLineIdAndStationId(long lineId, long stationId) {
        String sql = "select * from section where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        return !jdbcTemplate.query(sql, sectionMapper.getRowMapper(), lineId, stationId, stationId).isEmpty();
    }
}
