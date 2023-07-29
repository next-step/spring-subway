package subway.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.*;

import javax.sql.DataSource;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;

@Repository
public class SectionDao {

    private static final String ROW_MAPPER_BASE_SQL =
            " SELECT s.id as section_id, "
                    + " s.distance as distance, "
                    + " l.id as line_id, "
                    + " l.name as line_name, "
                    + " l.color as line_color, "
                    + " u.id as up_id, "
                    + " u.name as up_name, "
                    + " d.id as down_id, "
                    + " d.name as down_name "
                    + " FROM SECTION as s "
                    + " INNER JOIN LINE as l ON l.id = s.line_id "
                    + " INNER JOIN STATION as u ON u.id = s.up_station_id "
                    + " INNER JOIN STATION as d ON d.id = s.down_station_id ";
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final RowMapper<Section> rowMapper = (rs, rowNum) -> {
        final Line line = new Line(
                rs.getLong("line_id"),
                rs.getString("line_name"),
                rs.getString("line_color")
        );
        final Station upStation = new Station(
                rs.getLong("up_id"),
                rs.getString("up_name")
        );
        final Station donwStation = new Station(
                rs.getLong("down_id"),
                rs.getString("down_name")
        );
        final Distance distance = new Distance(
                rs.getLong("distance")
        );
        return new Section(
                rs.getLong("section_id"),
                line,
                upStation,
                donwStation,
                distance);
    };

    public SectionDao(final JdbcTemplate jdbcTemplate, final DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("section")
                .usingGeneratedKeyColumns("id");
    }

    public Section insert(final Section section) {
        final SqlParameterSource params = new BeanPropertySqlParameterSource(section);
        final Long id = insertAction.executeAndReturnKey(params).longValue();
        return new Section(
                id,
                section.getLine(),
                section.getUpStation(),
                section.getDownStation(),
                new Distance(section.getDistance()));
    }

    public List<Section> findAllByLineId(final Long lineId) {
        final String sql = ROW_MAPPER_BASE_SQL + " where s.line_id = ? ";
        return jdbcTemplate.query(sql, rowMapper, lineId);
    }

    public List<Section> findAll() {
        return jdbcTemplate.query(ROW_MAPPER_BASE_SQL, rowMapper);
    }

    public boolean existSectionByLineId(final Long lineId) {
        final String sql = "select count(*) > 0 from section where line_id = ? ";
        return TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, lineId));
    }

    public void deleteById(final Long sectionId) {
        final String sql = "delete from section where id = ?";
        jdbcTemplate.update(sql, sectionId);
    }

    public void updateSections(final Long lineId, final Sections sections) {
        dirtyChecking(findAllByLineId(lineId), sections.getSections());
    }

    private void dirtyChecking(final List<Section> beforeSection, final List<Section> afterSection) {
        deleteChecking(beforeSection, afterSection);
        insertChecking(beforeSection, afterSection);
    }

    private void insertChecking(final List<Section> beforeSection, final List<Section> afterSection) {
        final List<Section> insertingSections = afterSection.stream()
                .filter(section -> !beforeSection.contains(section))
                .filter(Section::isNew)
                .collect(toList());

        for (Section section : insertingSections) {
            insert(section);
        }
    }

    private void deleteChecking(final List<Section> beforeSection, final List<Section> afterSection) {
        final List<Section> deletingSections = beforeSection.stream()
                .filter(section -> !afterSection.contains(section))
                .collect(toList());

        for (Section section : deletingSections) {
            deleteById(section.getId());
        }
    }

    public void deleteAll() {
        final String sql = "delete from section";
        jdbcTemplate.update(sql);
    }
}
