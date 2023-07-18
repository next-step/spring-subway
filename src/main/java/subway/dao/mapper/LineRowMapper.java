package subway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Line;

@Component
public class LineRowMapper implements RowMapper<Line> {

    @Override
    public Line mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("LINE.id");
        String name = rs.getString("LINE.name");
        String color = rs.getString("LINE.color");

        return new Line(id, name, color);
    }
}