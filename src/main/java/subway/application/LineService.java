package subway.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.LineWithSection;
import subway.domain.LineWithSections;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationsDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationsDao;
    }

    public LineResponse saveLine(final LineRequest request) {
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        sectionDao.insert(
                new Section(persistLine.getId(), request.getUpStationId(), request.getDownStationId(), request.getDistance()));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = findLines();
        return persistLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public List<Line> findLines() {
        return lineDao.findAll();
    }

    public LineResponse findLineAndStationsById(final Long id) {
        final LineWithSections lineWithSections = new LineWithSections(findAllById(id));

        final List<Long> sortedStationIds = lineWithSections.getSortedStationIds();
        final Map<Long, Station> stations = stationDao.findAllByStationIdIn(sortedStationIds).stream()
                .collect(Collectors.toMap(Station::getId, station -> station));

        return LineResponse.of(lineWithSections.getLine(), sortedStationIds, stations);
    }

    public List<LineWithSection> findAllById(Long id) {
        return lineDao.findAllById(id);
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

}
