package subway.dao;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    rs.getLong("distance")
            );

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section insert(Section section) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(section);
        Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Section(
                id,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance());
    }

    public Section findLastSection(long lineId) {
        String sql = "select * from section s1 where s1.line_id = ? "
                + "and not exists (select * from section s2 where s1.down_station_id = s2.up_station_id)";

        return jdbcTemplate.queryForObject(sql, rowMapper, lineId);
    }

    public boolean existByLineId(long lineId) {
        String sql = "select count(*) from section where line_id = ? ";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, lineId));
    }

    public boolean existByLineIdAndStationId(long lineId, long stationId) {
        String sql = "select count(*) from section where line_id = ? and (down_station_id = ? or up_station_id = ?)";

        return Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(sql, Boolean.class, lineId, stationId, stationId));
    }

    public List<Section> findAllByLineId(long lineId) {
        String sql = "select * from section where line_id = ? ";
        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public void deleteById(Long id) {
        String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<Section> findByLineIdAndUpStationId(long lineId, long upStationId) {
        String sql = "select * from section where line_id = ? and up_station_id = ?";
        return Optional.ofNullable(DataAccessUtils.singleResult(
                jdbcTemplate.query(sql, rowMapper, lineId, upStationId)));
    }

    public Optional<Section> findByLineIdAndDownStationId(long lineId, long downStationId) {
        String sql = "select * from section where line_id = ? and down_station_id = ?";
        return Optional.ofNullable(DataAccessUtils.singleResult(
                jdbcTemplate.query(sql, rowMapper, lineId, downStationId)));
    }
}
