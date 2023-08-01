package subway.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import subway.domain.Distance;
import subway.domain.Section;
import subway.domain.Station;

@Repository
public class SectionDao {

    public static final String selectSectionSql = "select section.id as section_id, " +
        "up_station.id as up_station_id, " +
        "up_station.name as up_station_name, " +
        "down_station.id as down_station_id, " +
        "down_station.name as down_station_name, " +
        "section.distance as section_distance " +
        "from SECTION section " +
        "left join STATION up_station on section.up_station_id = up_station.id " +
        "left join STATION down_station on section.down_station_id = down_station.id " +
        "where section.id = ?";
    public static final String selectSectionsSql = "select section.id as section_id, " +
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

    public static final String selectSectionsAllSql = "select section.id as section_id, " +
        "up_station.id as up_station_id, " +
        "up_station.name as up_station_name, " +
        "down_station.id as down_station_id, " +
        "down_station.name as down_station_name, " +
        "section.distance as section_distance " +
        "from SECTION section " +
        "left join STATION up_station on section.up_station_id = up_station.id " +
        "left join STATION down_station on section.down_station_id = down_station.id ";

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertAction;
    private final ResultSetExtractor<Section> sectionExtractor = rs -> {
        rs.next();
        return new Section(
            rs.getLong("section_id"),
            new Station(rs.getLong("up_station_id"), rs.getString("up_station_name")),
            new Station(rs.getLong("down_station_id"), rs.getString("down_station_name")),
            new Distance(rs.getInt("section_distance"))
        );
    };

    private final ResultSetExtractor<List<Section>> sectionsExtractor = rs -> {
        List<Section> sections = new ArrayList<>();
        while (rs.next()) {
            sections.add(
                new Section(
                    rs.getLong("section_id"),
                    new Station(rs.getLong("up_station_id"), rs.getString("up_station_name")),
                    new Station(rs.getLong("down_station_id"), rs.getString("down_station_name")),
                    new Distance(rs.getInt("section_distance"))
                )
            );
        }
        return sections;
    };


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

    public Optional<Section> selectSection(final Long sectionId) {

        try {
            Section section = jdbcTemplate.query(selectSectionSql, sectionExtractor, sectionId);
            return Optional.of(section);
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Section>> selectSections(final Long lindId) {
        try {
            List<Section> sections = jdbcTemplate.query(selectSectionsSql, sectionsExtractor,
                lindId);
            return Optional.of(sections);
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Section>> findAll() {
        try {
            List<Section> sections = jdbcTemplate.query(selectSectionsAllSql, sectionsExtractor);
            return Optional.of(sections);
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
    }
}
