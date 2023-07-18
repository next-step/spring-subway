package subway.dao;

import static subway.dao.SectionDao.SectionLineBatchPreparedStatementSetter.SECTION_LINE_INSERT_SQL;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Line;
import subway.domain.Section;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    SectionDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("sections")
                .usingGeneratedKeyColumns("id");
    }

    public Section insert(Section section) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("up_section_id", extractUpSectionId(section));
        params.put("down_section_id", extractDownSectionId(section));
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("distance", section.getDistance());

        Long id = insertAction.executeAndReturnKey(params).longValue();
        Section savedSection = buildSection(id, section);

        jdbcTemplate.batchUpdate(SECTION_LINE_INSERT_SQL, new SectionLineBatchPreparedStatementSetter(savedSection));
        return savedSection;
    }

    private Long extractUpSectionId(Section section) {
        if (section.getUpSection() != null) {
            return section.getUpSection().getId();
        }
        return null;
    }

    private Long extractDownSectionId(Section section) {
        if (section.getDownSection() != null) {
            return section.getDownSection().getId();
        }
        return null;
    }

    private Section buildSection(Long sectionId, Section section) {
        return Section.builder()
                .id(sectionId)
                .lines(section.getLines())
                .upStation(section.getUpStation())
                .downStation(section.getDownStation())
                .upSection(section.getUpSection())
                .downSection(section.getDownSection())
                .build();
    }

    static final class SectionLineBatchPreparedStatementSetter implements BatchPreparedStatementSetter {

        static final String SECTION_LINE_INSERT_SQL = "INSERT INTO SECTIONS_LINE(section_id, line_id) VALUES(?, ?)";
        private final Section section;

        private SectionLineBatchPreparedStatementSetter(Section section) {
            this.section = section;
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            for (Line line : section.getLines()) {
                ps.setLong(1, section.getId());
                ps.setLong(2, line.getId());
            }
        }

        @Override
        public int getBatchSize() {
            return section.getLines().size();
        }
    }

}
