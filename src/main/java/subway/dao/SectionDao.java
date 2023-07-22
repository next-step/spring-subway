package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static org.springframework.dao.support.DataAccessUtils.singleResult;

@Repository
public class SectionDao {

    private static final String ROW_MAPPER_SQL =
            " SELECT s.id as section_id, "
                    + " s.distance as distance, "
                    + " l.id as line_id, "
                    + " l.name as line_name, "
                    + " l.color as line_color, "
                    + " u.id as up_id, "
                    + " u.name as up_name, "
                    + " d.id as down_id, "
                    + " d.name as down_name "
                    + " FROM SECTION as s "
                    + " INNER JOIN LINE as l ON l.id = s.line_id "
                    + " INNER JOIN STATION as u ON u.id = s.up_station_id "
                    + " INNER JOIN STATION as d ON d.id = s.down_station_id ";
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private RowMapper<Section> rowMapper = (rs, rowNum) -> {
        final Line line = new Line(
                rs.getLong("line_id"),
                rs.getString("line_name"),
                rs.getString("line_color")
        );
        final Station upStation = new Station(
                rs.getLong("up_id"),
                rs.getString("up_name")
        );
        final Station donwStation = new Station(
                rs.getLong("down_id"),
                rs.getString("down_name")
        );
        final Distance distance = new Distance(
                rs.getLong("distance")
        );
        return new Section(
                rs.getLong("section_id"),
                line,
                upStation,
                donwStation,
                distance);
    };

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section insert(final Section section) {
        final SqlParameterSource params = new BeanPropertySqlParameterSource(section);
        final Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Section(
                id,
                section.getLine(),
                section.getUpStation(),
                section.getDownStation(),
                new Distance(section.getDistance()));
    }

    public boolean existAllOrNotingInLineBySection(final Line line, final Section section) {
        final String sql = "SELECT count(*) FROM SECTION WHERE line_id = ? and " +
                " (up_station_id = ? or down_station_id = ?) ";
        final boolean existUpStationCount = TRUE.equals(jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                line.getId(),
                section.getUpStationId(), section.getUpStationId()));
        final boolean existDownStationCount = TRUE.equals(jdbcTemplate.queryForObject(
                sql,
                Boolean.class,
                line.getId(),
                section.getDownStationId(), section.getDownStationId()));
        return existUpStationCount == existDownStationCount;
    }

    public Optional<Section> findSectionByUpStation(final Line line, final Station upStation) {
        final String sql = ROW_MAPPER_SQL + " WHERE line_id = ? and up_station_id = ?";
        final Section result = singleResult(jdbcTemplate.query(
                sql,
                rowMapper,
                line.getId(),
                upStation.getId()));
        return Optional.ofNullable(result);
    }

    public Optional<Section> findSectionByDownStation(final Line line, final Station downStation) {
        final String sql = ROW_MAPPER_SQL + " WHERE line_id = ? and down_station_id = ?";
        final Section result = singleResult(jdbcTemplate.query(
                sql,
                rowMapper,
                line.getId(),
                downStation.getId()));
        return Optional.ofNullable(result);
    }

    public List<Section> findAllByLineId(final Long lineId) {
        final String sql = ROW_MAPPER_SQL + " where s.line_id = ? ";
        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public void deleteById(final Long id) {
        final String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
