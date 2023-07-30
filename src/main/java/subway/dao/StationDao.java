package subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Station;
import subway.domain.StationName;

@Repository
public class StationDao {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Station> rowMapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );

    public StationDao(final NamedParameterJdbcTemplate namedJdbcTemplate, final DataSource dataSource) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station insert(final Station station) {
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", station.getStationName().getValue());

        Long id = insertAction.executeAndReturnKey(params).longValue();

        return new Station(id, station.getStationName());
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";

        return namedJdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Station> findById(final Long id) {
        String sql = "select * from STATION where id = :id";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        try {
            return Optional.ofNullable(namedJdbcTemplate.queryForObject(sql, params, rowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean exists(final StationName stationName) {
        String sql = "select exists(select id from STATION where name = :name)";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", stationName.getValue());

        return Boolean.TRUE.equals(namedJdbcTemplate.queryForObject(sql, params, Boolean.class));
    }

    public void update(final Station newStation) {
        String sql = "update STATION set name = :name where id = :id";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", newStation.getStationName().getValue())
                        .addValue("id", newStation.getId());

        namedJdbcTemplate.update(sql, params);
    }

    public void deleteById(final Long id) {
        String sql = "delete from STATION where id = :id";
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        namedJdbcTemplate.update(sql, params);
    }
}
