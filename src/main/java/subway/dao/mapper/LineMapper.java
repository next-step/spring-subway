package subway.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Line;

@Component
public final class LineMapper {

    private final RowMapper<Line> rowMapper = (rs, rowNum) ->
        new Line(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("color")
        );

    public RowMapper<Line> getRowMapper() {
        return rowMapper;
    }
}
