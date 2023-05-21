package subway.persistence.jdbcDao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Section;
import subway.domain.repository.SectionRepository;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SectionDao implements SectionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final SectionRowMapper rowMapper;

    public SectionDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("SECTION")
                .usingGeneratedKeyColumns("id");
        rowMapper = new SectionRowMapper(new StationDao(dataSource), new LineDao(dataSource));
    }

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
