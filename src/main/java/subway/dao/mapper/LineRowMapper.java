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
        long id = rs.getLong("id");
        String name = rs.getString("name");
        String color = rs.getString("color");

        return new Line(id, name, color);
    }
}