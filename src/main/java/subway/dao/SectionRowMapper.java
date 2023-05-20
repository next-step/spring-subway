package subway.dao;

import org.springframework.jdbc.core.RowMapper;
import subway.domain.Distance;
import subway.domain.Section;
import subway.domain.Station;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SectionRowMapper implements RowMapper<Section> {

    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
        Station upStation = new Station(
                rs.getLong("up_station_id"),
                rs.getString("up_station_name")
        );
        Station downStation = new Station(
                rs.getLong("down_station_id"),
                rs.getString("down_station_name")
        );

        return Section.builder()
                .id(rs.getLong("section_id"))
                .lineId(rs.getLong("line_id"))
                .downStation(downStation)
                .upStation(upStation)
                .distance(Distance.of(rs.getInt("distance")))
                .build();
    }
}
