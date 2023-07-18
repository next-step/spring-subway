package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;

import javax.sql.DataSource;
import java.util.Optional;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("down_station_id"),
                    rs.getLong("up_station_id"),
                    rs.getDouble("distance")
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
        return new Section(id, section.getLineId(), section.getDownStationId(), section.getUpStationId(), section.getDistance());
    }

    public Optional<Section> findByDownStationIdAndLineId(final long lineId, final long downStationId) {
        String sql = "select * from section where line_id = ? and down_station_id = ?";
        return jdbcTemplate.query(sql, rowMapper, lineId, downStationId)
                .stream()
                .findAny();
    }

    public Optional<Section> findByUpStationIdAndLineId(final long lineId, final long upStationId) {
        String sql = "select * from section where line_id = ? and up_station_id = ?";
        return jdbcTemplate.query(sql, rowMapper, lineId, upStationId)
                .stream()
                .findAny();
    }

    public Optional<Section> findByLineIdAndStationId(final long lineId, final long stationId) {
        String sql = "select * from section where line_id = ? and (up_station_id = ? or down_station_id = ?)";
        return jdbcTemplate.query(sql, rowMapper, lineId, stationId, stationId)
                .stream()
                .findAny();
    }
}
