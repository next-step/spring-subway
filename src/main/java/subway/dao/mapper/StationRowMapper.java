package subway.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Station;

@Component
public class StationRowMapper implements RowMapper<Station> {

    @Override
    public Station mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");

        return new Station(id, name);
    }

    public Station mapRow(ResultSet rs, String alias, Long stationId) throws SQLException {
        if (stationId == null) {
            return null;
        }
        Long id = rs.getLong(alias + "_ID");
        String name = rs.getString(alias + "_NAME");

        return new Station(id, name);
    }
}