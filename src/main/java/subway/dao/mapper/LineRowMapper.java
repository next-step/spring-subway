package subway.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Line;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class LineRowMapper implements RowMapper<Line> {

    @Override
    public Line mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final long id = rs.getLong("id");
        final String name = rs.getString("name");
        final String color = rs.getString("color");

        return new Line(id, name, color);
    }

    public Line mapRow(final ResultSet rs, final String alias) throws SQLException {
        final long id = rs.getLong(alias + "_id");
        final String name = rs.getString(alias + "_name");
        final String color = rs.getString(alias + "_color");

        return new Line(id, name, color);
    }
}
