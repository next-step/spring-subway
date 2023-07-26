package subway.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@Component
public final class SectionMapper {

    private final RowMapper<Section> rowMapper = (rs, rowNum) -> {
        Line line = new Line(
            rs.getLong("line_id"),
            rs.getString("line_name"),
            rs.getString("line_color")
        );
        Station upStation = new Station(
            rs.getLong("up_station_id"),
            rs.getString("up_station_name")
        );
        Station downStation = new Station(
            rs.getLong("down_station_id"),
            rs.getString("down_station_name")
        );

        return new Section(
            rs.getLong("id"),
            line,
            upStation,
            downStation,
            rs.getInt("distance")
        );
    };

    public RowMapper<Section> getRowMapper() {
        return rowMapper;
    }
}
