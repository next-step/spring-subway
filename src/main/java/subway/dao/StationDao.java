package subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.dao.mapper.StationMapper;
import subway.domain.Station;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final StationMapper stationMapper;


    public StationDao(JdbcTemplate jdbcTemplate, DataSource dataSource, StationMapper stationMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("station")
                .usingGeneratedKeyColumns("id");
        this.stationMapper = stationMapper;
    }

    public Station insert(Station station) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(station);
        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Station(id, station.getName());
    }

    public List<Station> findAll() {
        String sql = "SELECT * FROM station";
        return jdbcTemplate.query(sql, stationMapper.getRowMapper());
    }

    public Optional<Station> findById(Long id) {
        String sql = "SELECT * FROM station WHERE id = ?";
        return jdbcTemplate.query(sql, stationMapper.getRowMapper(), id)
            .stream().findAny();
    }

    public void update(Station newStation) {
        String sql = "UPDATE station SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, newStation.getName(), newStation.getId());
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
