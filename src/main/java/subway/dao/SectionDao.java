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
import subway.domain.Section;
import subway.domain.Sections;
import subway.exception.ErrorCode;
import subway.exception.StationException;

@Repository
public class SectionDao {

    private static final String NO_UP_STATION_EXCEPTION_MESSAGE = "상행역이 존재하지 않습니다.";
    private static final String NO_DOWN_STATION_EXCEPTION_MESSAGE = "하행역이 존재하지 않습니다.";

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Section> rowMapper;
    private final RowMapper<Long> idRowMapper;

    public SectionDao(final NamedParameterJdbcTemplate namedJdbcTemplate, final DataSource dataSource, final StationDao stationDao) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        this.rowMapper = (rs, rowNum) ->
                new Section(
                        rs.getLong("id"),
                        stationDao.findById(rs.getLong("up_station_id"))
                                .orElseThrow(() -> new StationException(ErrorCode.NO_SUCH_STATION, NO_UP_STATION_EXCEPTION_MESSAGE)),
                        stationDao.findById(rs.getLong("down_station_id"))
                                .orElseThrow(() -> new StationException(ErrorCode.NO_SUCH_STATION, NO_DOWN_STATION_EXCEPTION_MESSAGE)),
                        rs.getInt("distance")
                );
        this.idRowMapper = (rs, rowNum) -> rs.getLong("id");
    }

    public void insert(final Section section, final Long lineId) {
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", section.getId())
                .addValue("up_station_id", section.getUpStation().getId())
                .addValue("down_station_id", section.getDownStation().getId())
                .addValue("line_id", lineId)
                .addValue("distance", section.getDistance().getValue());

        insertAction.executeAndReturnKey(params);

    }

    public Optional<Long> findIdByStationIdsAndLineId(final Long upStationId, final Long downStationId, final Long lineId) {
        final String sql = "select id from SECTION where up_station_id = :u_id and down_station_id = :d_id and line_id = :l_id";
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("u_id", upStationId)
                .addValue("d_id", downStationId)
                .addValue("l_id", lineId);

        try {
            return Optional.ofNullable(namedJdbcTemplate.queryForObject(sql, params, idRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Sections> findAllByLineId(final Long lineId) {
        final String sql = "select * from SECTION where line_id = :l_id";
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("l_id", lineId);

        try {
            return Optional.of(new Sections(namedJdbcTemplate.query(sql, params, rowMapper)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteAllIn(final List<Long> ids) {
        final String sql = "delete from SECTION where id in (:removeIds)";
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("removeIds", ids);

        namedJdbcTemplate.update(sql, params);
    }

    public void deleteById(final Long id) {
        final String sql = "delete from SECTION where id = :id";
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        namedJdbcTemplate.update(sql, params);
    }
}
