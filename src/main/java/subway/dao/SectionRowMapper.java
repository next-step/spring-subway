package subway.dao;

import org.springframework.jdbc.core.RowMapper;
import subway.domain.Section;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SectionRowMapper implements RowMapper<Section> {

    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Section.builder()
                .id(rs.getLong("id"))
                .lineId(rs.getLong("line_id"))
                .downStationId(rs.getLong("down_station_id"))
                .upStationId(rs.getLong("up_station_id"))
                .distance(rs.getInt("distance"))
                .build();
    }
}
