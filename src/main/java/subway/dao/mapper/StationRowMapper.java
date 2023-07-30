package subway.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Station;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class StationRowMapper implements RowMapper<Station> {

    @Override
    public Station mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final long id = rs.getLong("id");
        final String name = rs.getString("name");

        return new Station(id, name);
    }

    public Station mapRow(final ResultSet rs, final String alias) throws SQLException {
        final long id = rs.getLong(alias + "_id");
        final String name = rs.getString(alias + "_name");

        return new Station(id, name);
    }
}
