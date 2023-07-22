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

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private RowMapper<Station> rowMapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("id"),
                    rs.getString("name")
            );


    public StationDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
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

    public List<Station> findAllByLineId(final Long lineId) {
        final String upStationSql = "SELECT up_station.id AS id, " +
                "up_station.name AS name " +
                "FROM SECTION section " +
                "LEFT JOIN Line line ON section.LINE_ID = line.ID " +
                "LEFT JOIN STATION up_station ON section.up_station_id = up_station.id " +
                "WHERE line.id = ?";

        final String downStationSql = "SELECT down_station.id AS id, " +
                "down_station.name AS name " +
                "FROM SECTION section " +
                "LEFT JOIN Line line ON section.LINE_ID = line.ID " +
                "LEFT JOIN STATION down_station ON section.down_station_id = down_station.id " +
                "WHERE line.id = ?";

        final String sql = "SELECT id, name " +
                "FROM (" + upStationSql + "UNION " + downStationSql + ")";

        return jdbcTemplate.query(sql, rowMapper, lineId, lineId);
    }

    public Station findById(Long id) {
        String sql = "select * from STATION where id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public void update(Station newStation) {
        String sql = "update STATION set name = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{newStation.getName(), newStation.getId()});
    }

    public void deleteById(Long id) {
        String sql = "delete from STATION where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
