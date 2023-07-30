package subway.dao;

import java.util.ArrayList;
import java.util.Collections;
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
import subway.domain.Distance;
import subway.domain.Path;
import subway.domain.Station;
import subway.domain.StationName;

@Repository
public class PathDao {

    private static final MapSqlParameterSource NO_PARAM_SOURCE = new MapSqlParameterSource();
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final SimpleJdbcInsert pathInsertAction;
    private final RowMapper<Path> pathRowMapper;
    private final RowMapper<Station> stationRowMapper;

    public PathDao(final NamedParameterJdbcTemplate namedJdbcTemplate, final DataSource dataSource) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.pathInsertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("path")
                .usingGeneratedKeyColumns("id");
        this.pathRowMapper = (rs, rowNum) ->
                new Path(
                        rs.getLong("id"),
                        new Distance(rs.getInt("distance"))
                );
        this.stationRowMapper = (rs, rowNum) ->
                new Station(
                        rs.getLong("waypoint_id"),
                        new StationName(rs.getString("name"))
                );
    }

    public Optional<Path> findPathBySourceAndTarget(final Station source, final Station target) {
        String sql = "select id, distance from PATH where source_id = :s_id and target_id = :t_id";
        SqlParameterSource params = sortParamsByAsc(source, target);

        try {
            return Optional.ofNullable(namedJdbcTemplate.queryForObject(sql, params, pathRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private SqlParameterSource sortParamsByAsc(final Station source, final Station target) {
        if (source.getId() < target.getId()) {
            return new MapSqlParameterSource()
                    .addValue("s_id", source.getId())
                    .addValue("t_id", target.getId());
        }

        return new MapSqlParameterSource()
                .addValue("s_id", target.getId())
                .addValue("t_id", source.getId());
    }

    public List<Station> findWaypointsByPathId(final Long pathId, final boolean isDesc) {
        String sql = "select pd.waypoint_id, s.name from DETAIL_PATH pd"
                + " join STATION s on pd.waypoint_id = s.id"
                + " where pd.path_id = :p_id"
                + " order by pd.sequence" + (isDesc ? " desc" : "");
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("p_id", pathId);

        return namedJdbcTemplate.query(sql, params, stationRowMapper);
    }

    public long insertPathForCache(final Station source, final Station target, final Distance distance) {
        SqlParameterSource params = sortedParamsInOrder(source, target)
                .addValue("distance", distance.getValue());

        return pathInsertAction.executeAndReturnKey(params).longValue();
    }

    private MapSqlParameterSource sortedParamsInOrder(final Station source, final Station target) {
        if (source.getId() < target.getId()) {
            return new MapSqlParameterSource()
                    .addValue("source_id", source.getId())
                    .addValue("target_id", target.getId());
        }

        return new MapSqlParameterSource()
                .addValue("source_id", target.getId())
                .addValue("target_id", source.getId());
    }

    public void insertWaypointsForCache(final long pathId, final List<Long> stationIdsInOrder, boolean isDesc) {
        String sql = "insert into DETAIL_PATH (path_id, waypoint_id, sequence) values (:p_id, :w_id, :seq)";

        List<Long> waypoints = new ArrayList<>(stationIdsInOrder);
        if (isDesc) {
            Collections.reverse(waypoints);
        }

        List<SqlParameterSource> batchParams = new ArrayList<>();

        for (int i = 0; i < waypoints.size(); i++) {
            batchParams.add(new MapSqlParameterSource()
                    .addValue("p_id", pathId)
                    .addValue("w_id", waypoints.get(i))
                    .addValue("seq", i));
        }

        namedJdbcTemplate.batchUpdate(sql, batchParams.toArray(new SqlParameterSource[0]));
    }

    public void flushCache() {
        String sql = "delete from PATH";

        namedJdbcTemplate.update(sql, NO_PARAM_SOURCE);
    }
}
