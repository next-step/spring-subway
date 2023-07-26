package subway.dao;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class LineDao {
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Line> lineRowMapper = (rs, rowNum) ->
            new Line(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("color")
            );

    private final ResultSetExtractor<Line> extractor = rs -> {
        List<Section> sections = new ArrayList<>();
        boolean isNext = rs.next();

        Long id = rs.getLong("line_id");
        String name = rs.getString("line_name");
        String color = rs.getString("line_color");

        while (isNext) {
            sections.add(
                    new Section(
                            rs.getLong("section_id"),
                            new Station(rs.getLong("up_station_id"), rs.getString("up_station_name")),
                            new Station(rs.getLong("down_station_id"), rs.getString("down_station_name")),
                            new Distance(rs.getInt("section_distance"))
                    )
            );
            isNext = rs.next();
        }

        return new Line(id, name, color, new Sections(sections)
        );
    };

    public LineDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
    }

    public Line insert(final Line line) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", line.getId());
        params.put("name", line.getName());
        params.put("color", line.getColor());

        try {
            Long lineId = insertAction.executeAndReturnKey(params).longValue();
            return new Line(lineId, line.getName(), line.getColor());
        } catch (DuplicateKeyException e) {
            throw new SubwayException(ErrorCode.LINE_NAME_DUPLICATE, line.getName(), e);
        }
    }

    public List<Line> findAll() {
        String sql = "select id, name, color from LINE";

        return jdbcTemplate.query(sql, lineRowMapper);
    }

    public Optional<Line> findById(final Long id) {
        String sql = "select section.id as section_id, " +
                "up_station.id as up_station_id, " +
                "up_station.name as up_station_name, " +
                "down_station.id as down_station_id, " +
                "down_station.name as down_station_name, " +
                "line.id as line_id, " +
                "line.name as line_name, " +
                "line.color as line_color," +
                "section.distance as section_distance " +
                "from SECTION section " +
                "left join LINE line on section.line_id=line.id " +
                "left join STATION up_station on section.up_station_id = up_station.id " +
                "left join STATION down_station on section.down_station_id = down_station.id " +
                "where section.line_id = ?";

        try {
            Line line = jdbcTemplate.query(sql, extractor, id);
            return Optional.of(line);
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
    }

    public void update(final Line line) {
        String sql = "update LINE set name = ?, color = ? where id = ?";
        try {
            jdbcTemplate.update(sql, line.getName(), line.getColor(), line.getId());
        } catch (DuplicateKeyException e) {
            throw new SubwayException(ErrorCode.LINE_NAME_DUPLICATE, line.getName(), e);
        }
    }

    public void deleteById(final Long id) {
        jdbcTemplate.update("delete from Line where id = ?", id);
    }
}
