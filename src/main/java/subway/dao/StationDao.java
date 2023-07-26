package subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Station;

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
        final SqlParameterSource params = new MapSqlParameterSource()
//                .addValue("id", station.getId())
                .addValue("name", station.getName().getValue());

        Long id = insertAction.executeAndReturnKey(params).longValue();

        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";

        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Station> findById(final Long id) {
        String sql = "select * from STATION where id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void update(final Station newStation) {
        String sql = "update STATION set name = ? where id = ?";

        jdbcTemplate.update(sql, new Object[]{newStation.getName().getValue(), newStation.getId()});
    }

    public void deleteById(final Long id) {
        String sql = "delete from STATION where id = ?";

        jdbcTemplate.update(sql, id);
    }
}
