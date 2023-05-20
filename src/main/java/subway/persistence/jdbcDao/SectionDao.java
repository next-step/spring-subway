package subway.persistence.jdbcDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.repository.SectionRepository;
import subway.domain.vo.Distance;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SectionDao implements SectionRepository {

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert insertAction;
    private StationDao stationDao;
    private LineDao lineDao;

    private RowMapper<Section> rowMapper = (rs, rowNum) -> {
        long lineId = rs.getLong("line_id");
        long upStationId = rs.getLong("up_station_id");
        long downStationId = rs.getLong("down_station_id");

        Line line = lineDao.findById(lineId);
        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);

        return Section.builder()
                .id(rs.getLong("id"))
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(new Distance(rs.getInt("distance")))
                .build();
    };

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
        this.stationDao = new StationDao(dataSource);
        this.lineDao = new LineDao(dataSource);
    }

//    @Autowired
//    public void setDataSource(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//        this.insertAction = new SimpleJdbcInsert(dataSource)
//                .withTableName("SECTION")
//                .usingGeneratedKeyColumns("id");
//    }

    @Override
    public Section insert(Section section) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("line_id", section.getLine().getId());
        parameters.put("up_station_id", section.getUpStation().getId());
        parameters.put("down_station_id", section.getDownStation().getId());
        parameters.put("distance", section.getDistance().getValue());
        long id = insertAction.executeAndReturnKey(parameters).longValue();
        section.setId(id);
        return section;
    }

    @Override
    public List<Section> findAll() {
        String sql = "select * from SECTION";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Section findById(Long id) {
        String sql = "select * from SECTION where id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from SECTION where id = ?";
        jdbcTemplate.update(sql, id);
    }
}
