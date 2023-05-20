package subway.dao;

import java.util.Map;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StationDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public StationDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station insert(Station station) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(station);
        Long id = jdbcInsert.executeAndReturnKey(param).longValue();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT id, name FROM station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Station findById(Long id) {
        String sql = "SELECT id, name FROM station WHERE id = :id";
        return jdbcTemplate.queryForObject(sql, Map.of("id", id), stationRowMapper);
    }

    public List<Station> findAllByLineId(Long lineId) {
        String sql = "SELECT DISTINCT st.id, st.name "
                    + "FROM station AS st "
                    + "WHERE EXISTS ( "
                    + "    SELECT 1 "
                    + "    FROM section AS se "
                    + "    WHERE se.line_id = :lineId "
                    + "    AND (st.id = se.up_station_id OR st.id = se.down_station_id))";
        return jdbcTemplate.query(sql, Map.of("lineId", lineId), stationRowMapper);
    }

    public void update(Station station) {
        String sql = "UPDATE station SET name = :name WHERE id = :id";
        SqlParameterSource param = new BeanPropertySqlParameterSource(station);
        jdbcTemplate.update(sql, param);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }

    private final RowMapper<Station> stationRowMapper = (rs, rowNum) ->
        new Station(
            rs.getLong("id"),
            rs.getString("name"));
}
