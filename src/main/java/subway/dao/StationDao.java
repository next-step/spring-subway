package subway.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Station;
import subway.domain.StationName;
import subway.exception.SubwayDataAccessException;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class StationDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Station> rowMapper = (rs, rowNum) ->
            new Station(
                    rs.getLong("id"),
                    new StationName(
                            rs.getString("name")
                    )
            );

    public StationDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("STATION")
                .usingGeneratedKeyColumns("id");
    }

    public Station insert(final Station station) {
        try {
            final Long id = insertAction.executeAndReturnKey(getStationParams(station)).longValue();

            return new Station(id, station.getName());
        } catch (Exception e) {
            throw new SubwayDataAccessException("이미 존재하는 역 이름입니다. 입력한 이름: " + station.getName().getValue(), e);
        }
    }

    public List<Station> findAll() {
        final String sql = "select * from STATION";

        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Station> findAllByIds(final List<Long> pathStationIds) {
        final String ids = pathStationIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "(", ")"));

        final String sql = "SELECT * FROM STATION WHERE id IN " + ids;

        return jdbcTemplate.query(sql, rowMapper);
    }

    public Optional<Station> findById(final Long id) {
        try {
            final String sql = "select * from STATION where id = ?";

            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (final EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void update(final Station newStation) {
        final int affectedRowCount = jdbcTemplate.update("update STATION set name = ? where id = ?", newStation.getName().getValue(), newStation.getId());
        if (affectedRowCount == 0) {
            throw new SubwayDataAccessException("역이 존재하지 않습니다. 입력한 식별자: " + newStation.getId());
        }

    }

    public void deleteById(final Long id) {
        final int affectedRowCount = jdbcTemplate.update("delete from STATION where id = ?", id);
        if (affectedRowCount == 0) {
            throw new SubwayDataAccessException("역이 존재하지 않습니다. 입력한 식별자: " + id);
        }
    }

    private Map<String, Object> getStationParams(final Station station) {
        final Map<String, Object> params = new HashMap<>();
        params.put("name", station.getName().getValue());

        return params;
    }
}
