package subway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Section;
import subway.domain.Station;

@Component
public class SectionRowMapper implements RowMapper<Section> {

    private final StationRowMapper stationRowMapper;

    public SectionRowMapper(StationRowMapper stationRowMapper) {
        this.stationRowMapper = stationRowMapper;
    }

    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("id");
        int distance = rs.getInt("distance");

        Station upStation = stationRowMapper.mapRow(rs, "US");
        Station downStation = stationRowMapper.mapRow(rs, "DS");

        return Section.builder()
                .id(id)
                .distance(distance)
                .upStation(upStation)
                .downStation(downStation)
                .build();
    }
}
