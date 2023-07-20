package subway.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.LineSections;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final StationDao stationDao;

    private RowMapper<Line> rowToLineMapper = (rs, rowNum) ->
        new Line(
            rs.getLong("line_id"),
            rs.getString("name"),
            rs.getString("color")
        );

    private RowMapper<SectionRow> rowToSectionRowMapper = (rs, rowNum) -> new SectionRow(
            rs.getLong("id"),
            rowToLineMapper.mapRow(rs, rowNum),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance")
        );

    public SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource, StationDao stationDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
        this.stationDao = stationDao;
    }

    public LineSections findAllByLineId(Long id){
        String sql = "select s.id, line_id, up_station_id, down_station_id, distance, "
            + "name, color "
            + "from SECTION s join LINE l on s.line_id = l.id "
            + "WHERE s.line_id = ?";

        List<Section> values = jdbcTemplate.query(sql, rowToSectionRowMapper, id).stream()
            .map(this::toSection)
            .collect(Collectors.toUnmodifiableList());

        return new LineSections(values.get(0).getLine(), new Sections(values));
    }

    public LineSections findAllByLine(Line line){
        String sql = "select s.id, line_id, up_station_id, down_station_id, distance, "
            + "name, color "
            + "from SECTION s join LINE l on s.line_id = l.id "
            + "WHERE s.line_id = ?";

        List<Section> values = jdbcTemplate.query(sql, rowToSectionRowMapper, line.getId()).stream()
            .map(this::toSection)
            .collect(Collectors.toUnmodifiableList());

        return new LineSections(line, new Sections(values));
    }


    private Section toSection(SectionRow sectionRow) {
        Station upStation = getStationOrElseThrow(sectionRow.getUpStationId());
        Station downStation = getStationOrElseThrow(sectionRow.getDownStationId());
        return new Section(sectionRow.getId(), sectionRow.getLine(), upStation,
            downStation, sectionRow.getDistance());
    }

    private Station getStationOrElseThrow(Long id) {
        return stationDao.findById(id)
            .orElseThrow(
                () -> new RuntimeException("존재하지 않는 station id입니다. id: \"" + id + "\""));
    }

    public Section save(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("line_id", section.getLine().getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        Long sectionId = insertAction.executeAndReturnKey(params).longValue();
        return new Section(sectionId, section.getLine(), section.getUpStation(), section.getDownStation(),
            section.getDistance());
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("delete from Section where id = ?", id);
    }

    public void delete(Section section) {
        jdbcTemplate.update("delete from Section where id = ?", section.getId());
    }
}
