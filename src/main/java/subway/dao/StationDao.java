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
    private final SimpleJdbcInsert insertAction;

    private RowMapper<Station> rowMapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );


    public StationDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
    }

    public Station insert(Station station) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(station);
        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        String sql = "select * from STATION";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Station findById(Long id) {
        String sql = "select * from STATION where id = :id";
        return jdbcTemplate.queryForObject(sql, Map.of("id", id), rowMapper);
    }

    public List<Station> findAllByLineId(Long lineId) {
        String sql = "SELECT DISTINCT st.id, st.name "
                    + "FROM station AS st "
                    + "WHERE EXISTS ( "
                    + "    SELECT 1 "
                    + "    FROM section AS se "
                    + "    WHERE se.line_id = :lineId "
                    + "    AND (st.id = se.up_station_id OR st.id = se.down_station_id))";
        return jdbcTemplate.query(sql, Map.of("lineId", lineId), rowMapper);
    }

    public void update(Station newStation) {
        String sql = "update STATION set name = :name where id = :id";
        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(newStation));
    }

    public void deleteById(Long id) {
        String sql = "delete from STATION where id = :id";
        jdbcTemplate.update(sql, Map.of("id", id));
    }
}
