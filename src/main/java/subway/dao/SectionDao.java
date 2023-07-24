package subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getInt("distance")
            );

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section insert(final Section section) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(section);
        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Section(id, section.getLineId(), section.getUpStationId(), section.getDownStationId(), section.getDistance());
    }

    public Optional<Section> findLastSection(final Long lineId) {
        String sql = "select * from SECTION S1 " +
                "where S1.line_id = ? " +
                "and not exists(select * from section S2 where S1.down_station_id = S2.up_station_id)";
        return jdbcTemplate.query(sql, rowMapper, lineId)
                .stream()
                .findAny();
    }

    public void deleteLastSection(final long lineId, final long stationId) {
        String sql = "delete from SECTION where line_id = ? and down_station_id = ?";
        jdbcTemplate.update(sql, lineId, stationId);
    }

    public long count(final long lineId) {
        String sql = "select * from section where line_id = ?";
        return jdbcTemplate.query(sql, rowMapper, lineId)
                .size();
    }

    public List<Section> findAll(final long lineId) {
        String sql = "select * from SECTION where line_id = ?";
        return jdbcTemplate.query(sql, rowMapper, lineId);
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
}
