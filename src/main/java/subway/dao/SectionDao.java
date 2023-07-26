package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Distance;
import subway.domain.Section;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class SectionDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;

    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
            new Section(
                    rs.getLong("id"),
                    rs.getLong("line_id"),
                    rs.getLong("up_station_id"),
                    rs.getLong("down_station_id"),
                    new Distance(
                            rs.getInt("distance")
                    )
            );

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section insert(Section section) {
        final Long sectionId = insertAction.executeAndReturnKey(generateEntry(section)).longValue();

        return new Section(sectionId, section);
    }

    public List<Section> insertAll(final List<Section> sections) {
        final List<Map<String, Object>> records = sections.stream()
                .map(this::generateEntry)
                .collect(Collectors.toList());

        final int[] ints = insertAction.executeBatch(SqlParameterSourceUtils.createBatch(records));

        return IntStream.range(0, sections.size())
                .mapToObj(i -> new Section((long) ints[i], sections.get(i)))
                .collect(Collectors.toList());
    }

    public List<Section> findAllByLineId(final Long lineId) {
        final String sql = "select * from SECTION where line_id = ?";

        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public int delete(final Long sectionId) {
        final String sql = "delete from SECTION where id = ?";

        return jdbcTemplate.update(sql, sectionId);
    }

    public int[] deleteAll(final List<Long> sectionIds) {
        return jdbcTemplate.batchUpdate(
                "delete from SECTION where id = ?",
                sectionIds.stream()
                        .map(id -> new Object[]{id})
                        .collect(Collectors.toList())
        );
    }

    private Map<String, Object> generateEntry(final Section section) {
        final Map<String, Object> entry = new HashMap<>();
        entry.put("id", section.getId());
        entry.put("line_id", section.getLineId());
        entry.put("up_station_id", section.getUpStationId());
        entry.put("down_station_id", section.getDownStationId());
        entry.put("distance", section.getDistance().getValue());

        return entry;
    }
}
