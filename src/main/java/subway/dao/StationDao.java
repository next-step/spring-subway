package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.springframework.dao.support.DataAccessUtils.singleResult;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Station> rowMapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    public StationDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station insert(final Station station) {
        final SqlParameterSource params = new BeanPropertySqlParameterSource(station);
        final Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        final String sql = "select * from STATION";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Station> findById(final Long stationId) {
        final String sql = "select * from STATION where id = ?";
        return Optional.ofNullable(singleResult(jdbcTemplate.query(sql, rowMapper, stationId)));
    }

    public void update(final Station newStation) {
        final String sql = "update STATION set name = ? where id = ?";
        jdbcTemplate.update(sql, newStation.getName(), newStation.getId());
    }

    public void deleteById(final Long stationId) {
        final String sql = "delete from STATION where id = ?";
        jdbcTemplate.update(sql, stationId);
    }
}
