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
import subway.domain.Station;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Station> rowMapper;

    public StationDao(JdbcTemplate jdbcTemplate, DataSource dataSource, RowMapper<Station> rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
        this.rowMapper = rowMapper;
    }

    public Station insert(Station station) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(station);
        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM STATION";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Station> findById(Long id) {
        String sql = "SELECT * FROM STATION WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
    }

    public void update(Station newStation) {
        String sql = "UPDATE STATION SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, newStation.getName(), newStation.getId());
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM STATION WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Station> findAllByLineId(Long lineId) {
        String sql = "SELECT DISTINCT S.* FROM STATION AS S "
                + "JOIN SECTIONS AS SE ON SE.line_id = ? AND (SE.up_station_id = S.id OR SE.down_station_id = S.id)";

        return jdbcTemplate.query(sql, rowMapper, lineId);
    }
}
