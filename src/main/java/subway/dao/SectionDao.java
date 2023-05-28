package subway.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final StationDao stationDao;

    private final RowMapper<Section> rowMapper;

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
        this.stationDao = stationDao;

        this.rowMapper = rowMapperProvider();
    }

    private RowMapper<Section> rowMapperProvider() {
        return (rs, rowNum) -> {
            Station upStation = stationDao.findById(rs.getLong("up_station_id"))
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Station 입니다."));
            Station downStation = stationDao.findById(rs.getLong("down_station_id"))
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Station 입니다."));

            return new Section(
                    rs.getLong("id"),
                    upStation,
                    downStation,
                    rs.getInt("distance")
            );
        };
    }

    public Section insert(Long lineId, Section section) {
        SqlParameterSource params = getParams(lineId, section);
        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Section(id, section.getUpStation(), section.getDownStation(), section.getDistance());
    }

    private SqlParameterSource getParams(Long lineId, Section section) {
        return new MapSqlParameterSource()
                .addValue("line_id", lineId)
                .addValue("up_station_id", section.getUpStation().getId())
                .addValue("down_station_id", section.getDownStation().getId())
                .addValue("distance", section.getDistance());
    }

    public List<Section> findAll() {
        String sql = "select * from section order by id";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Section> findAllByLineId(Long lineId) {
        String sql = "select * from section where line_id = ? order by id";
        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public Optional<Section> findById(Long id) {
        String sql = "select * from section where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (DataAccessException exception) {
            return Optional.empty();
        }
    }

    public void deleteById(Long id) {
        String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, id);
    }

}
