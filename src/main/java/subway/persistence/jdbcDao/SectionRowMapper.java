package subway.persistence.jdbcDao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.vo.Distance;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class SectionRowMapper implements RowMapper<Section> {

    private final StationDao stationDao;
    private final LineDao lineDao;

    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
        long lineId = rs.getLong("line_id");
        long upStationId = rs.getLong("up_station_id");
        long downStationId = rs.getLong("down_station_id");

        Line line = lineDao.findById(lineId);
        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);

        return Section.builder()
                .id(rs.getLong("id"))
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(new Distance(rs.getInt("distance")))
                .build();
    }
}
