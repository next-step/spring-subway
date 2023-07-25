package subway.dao.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class SectionRowMapper implements RowMapper<Section> {

    private final LineRowMapper lineRowMapper;
    private final StationRowMapper stationRowMapper;

    public SectionRowMapper(final LineRowMapper lineRowMapper, final StationRowMapper stationRowMapper) {
        this.lineRowMapper = lineRowMapper;
        this.stationRowMapper = stationRowMapper;
    }

    @Override
    public Section mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final long id = rs.getLong("id");
        final Line line = lineRowMapper.mapRow(rs, "line");
        final Station upStation = stationRowMapper.mapRow(rs, "up_station");
        final Station downStation = stationRowMapper.mapRow(rs, "down_station");
        final int distance = rs.getInt("distance");

        return new Section(id, line, upStation, downStation, distance);
    }
}
