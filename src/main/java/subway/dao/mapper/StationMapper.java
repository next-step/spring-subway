package subway.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Station;

@Component
public final class StationMapper {

    private final RowMapper<Station> rowMapper = (rs, rowNum) ->
        new Station(
            rs.getLong("id"),
            rs.getString("name")
        );

    public RowMapper<Station> getRowMapper() {
        return rowMapper;
    }
}
