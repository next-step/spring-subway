package subway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@Component
public class SectionRowMapper implements RowMapper<Section> {

    private final LineRowMapper lineRowMapper;
    private final StationRowMapper stationRowMapper;

    public SectionRowMapper(LineRowMapper lineRowMapper, StationRowMapper stationRowMapper) {
        this.lineRowMapper = lineRowMapper;
        this.stationRowMapper = stationRowMapper;
    }

    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        Integer distance = rs.getInt("distance");

        Line line = lineRowMapper.mapRow(rs, rowNum);

        Long upStationId = rs.getLong("up_station_id");
        Station upStation = stationRowMapper.mapRow(rs, rowNum, 9, 10, upStationId);

        Long downStationId = rs.getLong("down_station_id");
        Station downStation = stationRowMapper.mapRow(rs, rowNum, 11, 12, downStationId);

        return Section.builder()
                .id(id)
                .distance(distance)
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .build();
    }
}
