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
        Long id = rs.getLong("id");
        Integer distance = rs.getInt("distance");

        Long upStationId = rs.getLong("up_station_id");
        Station upStation = stationRowMapper.mapRow(rs, "US", upStationId);

        Long downStationId = rs.getLong("down_station_id");
        Station downStation = stationRowMapper.mapRow(rs, "DS", downStationId);

        return Section.builder()
                .id(id)
                .distance(distance)
                .upStation(upStation)
                .downStation(downStation)
                .build();
    }
}
