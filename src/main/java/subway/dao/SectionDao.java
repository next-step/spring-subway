package subway.dao;

import java.util.List;
import java.util.stream.Collectors;
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

    public Sections findAllByLineId(Long id){
        String sql = "select s.id, line_id, up_station_id, down_station_id, distance, "
            + "name, color "
            + "from SECTION s join LINE l on s.line_id = l.id "
            + "WHERE s.line_id = ?";

        List<Section> values = jdbcTemplate.query(sql, rowToSectionRowMapper, id).stream()
            .map(this::toSection)
            .collect(Collectors.toUnmodifiableList());
        return new Sections(values);
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

    /**
     * jdbcTemplate.queryForObject(sql, rowMapper, id)
     String sql = "select id, line_id, up_station_id, down_station_id, distance,  from LINE";
     *
     *
     * id bigint auto_increment not null,
     *     line_id not null,
     *     up_station_id not null,
     *     down_station_id not null,
     *     distance int,
     */


}
