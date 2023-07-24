package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import subway.domain.Station;
import subway.domain.StationPair;

import java.util.List;

@Repository
public class StationPairDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<StationPair> stationPairRowMapper = (rs, rowNum) ->
            new StationPair(
                    new Station(
                            rs.getLong("s1_id"),
                            rs.getString("s1_name")
                    ),
                    new Station(
                            rs.getLong("s2_id"),
                            rs.getString("s2_name")
                    )
            );

    public StationPairDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StationPair> findAllStationPair(final Long lineId) {
        String sql = "select s1.id as s1_id, s1.name as s1_name, s2.id as s2_id, s2.name as s2_name " +
                "from section " +
                "join station as s1 " +
                "on section.up_station_id = s1.id " +
                "join station as s2 " +
                "on section.down_station_id = s2.id " +
                "where line_id = ?";
        return jdbcTemplate.query(sql, stationPairRowMapper, lineId);
    }
}
