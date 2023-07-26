package subway.dao;

import java.util.List;
import javax.sql.DataSource;
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

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private RowMapper<Section> rowMapper = (rs, rowNum) -> {
        Line line = new Line(
                rs.getLong("line_id"),
                rs.getString("line_name"),
                rs.getString("line_color")
        );
        Station upStation = new Station(
                rs.getLong("up_id"),
                rs.getString("up_name")
        );

        Station downStation = new Station(
                rs.getLong("down_id"),
                rs.getString("down_name")
        );

        Distance distance = new Distance(
                rs.getLong("distance")
        );

        return new Section(
                rs.getLong("section_id"),
                line,
                upStation,
                downStation,
                distance);
    };

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
                section.getLine(),
                section.getUpStation(),
                section.getDownStation(),
                new Distance(section.getDistance()));
    }

    public List<Section> findAllByLineId(long lineId) {
        String sql = "select s.id as section_id, "
                + "s.distance as distance, "
                + "l.id as line_id, "
                + "l.name as line_name, "
                + "l.color as line_color, "
                + "u.id as up_id, "
                + "u.name as up_name, "
                + "d.id as down_id, "
                + "d.name as down_name "
                + " from section as s "
                + " INNER JOIN LINE as l ON l.id = s.line_id "
                + " INNER JOIN STATION as u ON u.id = s.up_station_id "
                + " INNER JOIN STATION as d ON d.id = s.down_station_id "
                + " where s.line_id = ? ";
        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public boolean existByLineId(Long lineId) {
        String sql = "select count(*) from section where line_id = ? ";

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, lineId));
    }

    public void update(Section section) {
        String sql = "update section set "
                + " line_id = ?, "
                + " up_station_id = ?, "
                + " down_station_id = ?, "
                + " distance = ? "
                + " where id = ? ";
        
        jdbcTemplate.update(sql,
                section.getLineId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance(),
                section.getId());
    }

    public void deleteById(Long id) {
        String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
