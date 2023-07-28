package subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private RowMapper<Line> rowToLineMapper = (rs, rowNum) ->
        new Line(
            rs.getLong("line_id"),
            rs.getString("line_name"),
            rs.getString("line_color")
        );

    private RowMapper<Station> rowToUpStationMapper = (rs, rowNum) ->
        new Station(
            rs.getLong("up_station_id"),
            rs.getString("up_station_name")
        );

    private RowMapper<Station> rowToDownStationMapper = (rs, rowNum) ->
        new Station(
            rs.getLong("down_station_id"),
            rs.getString("down_station_name")
        );

    private RowMapper<Section> rowToSectionMapper = (rs, rowNum) -> new Section(
        rs.getLong("section_id"),
        rowToLineMapper.mapRow(rs, rowNum),
        rowToUpStationMapper.mapRow(rs, rowNum),
        rowToDownStationMapper.mapRow(rs, rowNum),
        rs.getInt("distance")
    );

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
    }

    public Sections findAllBy(Line line) {
        String sql =
            "select s.id AS section_id, line_id, up_station_id, down_station_id, distance, "
                + "l.name AS line_name, color AS line_color, "
                + "us.name AS up_station_name, "
                + "ds.name AS down_station_name "
                + "from SECTION s join LINE l on s.line_id = l.id "
                + "join STATION us on up_station_id = us.id "
                + "join STATION ds on down_station_id = ds.id "
                + "WHERE s.line_id = ?";

        List<Section> values = jdbcTemplate.query(sql, rowToSectionMapper, line.getId());

        return new Sections(values);
    }

    public List<Section> findAll() {
        String sql =
            "select s.id AS section_id, line_id, up_station_id, down_station_id, distance, "
                + "l.name AS line_name, color AS line_color, "
                + "us.name AS up_station_name, "
                + "ds.name AS down_station_name "
                + "from SECTION s join LINE l on s.line_id = l.id "
                + "join STATION us on up_station_id = us.id "
                + "join STATION ds on down_station_id = ds.id";

        return jdbcTemplate.query(sql, rowToSectionMapper);
    }

    public Section save(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("line_id", section.getLine().getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        Long sectionId = insertAction.executeAndReturnKey(params).longValue();
        return new Section(sectionId, section.getLine(), section.getUpStation(),
            section.getDownStation(),
            section.getDistance());
    }

    public void delete(Section section) {
        jdbcTemplate.update("delete from SECTION where id = ?", section.getId());
    }

    public void deleteByLine(Line line) {
        jdbcTemplate.update("delete from SECTION where line_id = ?", line.getId());
    }

    public void deleteByLineAndStation(Line line, Station station) {
        jdbcTemplate.update(
            "delete from SECTION where line_id = ? and (up_station_id = ? or down_station_id = ?)",
            line.getId(), station.getId(), station.getId());
    }

    public void update(Section section) {
        String sql = "update SECTION set line_id = ?, up_station_id = ?, down_station_id = ?, distance = ? where id = ?";
        jdbcTemplate.update(sql, section.getLine().getId(), section.getUpStation().getId(),
            section.getDownStation().getId(), section.getDistance(), section.getId());

    }

    public Optional<Section> findById(Long id) {
        String sql =
            "select s.id AS section_id, line_id, up_station_id, down_station_id, distance, "
                + "l.name AS line_name, color AS line_color, "
                + "us.name AS up_station_name, "
                + "ds.name AS down_station_name "
                + "from SECTION s join LINE l on s.line_id = l.id "
                + "join STATION us on up_station_id = us.id "
                + "join STATION ds on down_station_id = ds.id "
                + "WHERE s.id = ?";

        return jdbcTemplate.queryForStream(sql, rowToSectionMapper, id)
            .findAny();
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from SECTION");
    }
}
