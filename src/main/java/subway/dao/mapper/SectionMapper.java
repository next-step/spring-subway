package subway.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Line;
import subway.domain.Section;

@Component
public final class SectionMapper {

    private final RowMapper<Section> rowMapper = (rs, rowNum) -> {
        Line line = new Line(
            rs.getLong("line_id"),
            rs.getString("line_name"),
            rs.getString("line_color")
        );
        return new Section(
            rs.getLong("id"),
            line,
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance")
        );
    };

    public RowMapper<Section> getRowMapper() {
        return rowMapper;
    }
}
