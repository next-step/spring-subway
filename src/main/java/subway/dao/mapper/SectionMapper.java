package subway.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Section;

@Component
public class SectionMapper {

    private final RowMapper<Section> rowMapper = (rs, rowNum) ->
        new Section(
            rs.getLong("id"),
            rs.getLong("line_id"),
            rs.getLong("up_station_id"),
            rs.getLong("down_station_id"),
            rs.getInt("distance")
        );

    public RowMapper<Section> getRowMapper() {
        return rowMapper;
    }
}
