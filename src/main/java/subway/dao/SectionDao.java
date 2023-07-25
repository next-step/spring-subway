package subway.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.domain.Sections;

@Repository
public class SectionDao {

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
                        stationDao.findById(rs.getLong("up_station_id")),
                        stationDao.findById(rs.getLong("down_station_id")),
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

    public Long findIdByStationIdsAndLineId(final Long upStationId, final Long downStationId, final Long lineId) {
        final String sql = "select id from SECTION where up_station_id = :u_id and down_station_id = :d_id and line_id = :l_id";
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("u_id", upStationId)
                .addValue("d_id", downStationId)
                .addValue("l_id", lineId);

        return namedJdbcTemplate.queryForObject(sql, params, idRowMapper);
    }

    public Sections findAllByLineId(final Long lineId) {
        final String sql = "select * from SECTION where line_id = :l_id";
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("l_id", lineId);

        return new Sections(namedJdbcTemplate.query(sql, params, rowMapper));
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
