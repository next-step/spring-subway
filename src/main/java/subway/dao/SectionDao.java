package subway.dao;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import subway.domain.Section;
import subway.domain.SectionRepository;
import subway.domain.Sections;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class SectionDao extends NamedParameterJdbcDaoSupport implements SectionRepository {

    private static final String TABLE_NAME = "section";
    private static final String ID = "id";
    private static final String[] COLUMNS = {"line_id", "down_station_id", "up_station_id", "distance"};
    private static final SectionRowMapper ROW_MAPPER = new SectionRowMapper();

    private final SimpleJdbcInsert simpleJdbcInsert;

    public SectionDao(DataSource dataSource) {
        setDataSource(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(TABLE_NAME)
                .usingGeneratedKeyColumns(ID)
                .usingColumns(COLUMNS);
    }

    @Override
    public Section save(Section section) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(section);
        Number key = simpleJdbcInsert.executeAndReturnKey(parameterSource);
        return Section.builder()
                .id(key.longValue())
                .lineId(section.getLineId())
                .downStationId(section.getDownStationId())
                .upStationId(section.getUpStationId())
                .distance(section.getDistance())
                .build();
    }

    @Override
    public Sections findAllByLineId(long lineId) {
        String query = String.format("SELECT * FROM %s WHERE line_id = :lineId", TABLE_NAME);
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("lineId", lineId);
        List<Section> sections = getNamedParameterJdbcTemplate().query(query, namedParameters, ROW_MAPPER);

        if (CollectionUtils.isEmpty(sections)) {
            return new Sections();
        }

        return new Sections(sections);
    }

    @Override
    public void deleteByLineIdAndDownStationId(long lineId, long stationId) {
        String query = String.format("DELETE FROM %s WHERE line_id = :lineId AND down_station_id = :stationId",
                TABLE_NAME);
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("lineId", lineId)
                .addValue("stationId", stationId);
        getNamedParameterJdbcTemplate().update(query, namedParameters);
    }
}
