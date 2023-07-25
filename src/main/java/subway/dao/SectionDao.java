package subway.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Distance;
import subway.domain.Section;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
            .withTableName("section")
            .usingGeneratedKeyColumns("id");
    }

    public Section insert(final Section section, final Long lineId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", section.getId());
        params.put("up_station_id", section.getUpStation().getId());
        params.put("down_station_id", section.getDownStation().getId());
        params.put("line_id", lineId);
        params.put("distance", section.getDistance());

        Long sectionId = insertAction.executeAndReturnKey(params).longValue();

        return new Section(sectionId, section.getUpStation(), section.getDownStation(),
            new Distance(section.getDistance()
            )
        );
    }


    public void deleteSections(final List<Section> deleteSections) {
        String sql = "DELETE FROM SECTION WHERE id = ?";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int idx)
                throws SQLException {
                preparedStatement.setLong(1, deleteSections.get(idx).getId());
            }

            @Override
            public int getBatchSize() {
                return deleteSections.size();
            }
        });
    }

    public void insertSections(final List<Section> insertSections, final Long lineId) {
        String sql = "INSERT INTO SECTION (up_station_id, down_station_id, line_id, distance) "
            + "VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int idx)
                throws SQLException {
                Section section = insertSections.get(idx);
                preparedStatement.setLong(1, section.getUpStation().getId());
                preparedStatement.setLong(2, section.getDownStation().getId());
                preparedStatement.setLong(3, lineId);
                preparedStatement.setLong(4, section.getDistance());
            }

            @Override
            public int getBatchSize() {
                return insertSections.size();
            }
        });
    }
}
