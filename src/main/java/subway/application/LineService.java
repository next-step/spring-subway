package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.ConnectedSections;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Line saveLine(final LineRequest request) {
        final Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        sectionDao.insert(
                new Section(
                        persistLine.getId(),
                        request.getUpStationId(),
                        request.getDownStationId(),
                        request.getDistance()
                )
        );

        return persistLine;
    }

    public List<LineResponse> findLineResponses() {
        final List<Line> persistLines = findLines();
        return persistLines.stream()
                .map(line -> findLineResponse(line.getId()))
                .collect(Collectors.toList());
    }

    public LineResponse findLineResponse(final Long lineId) {
        final Map<Long, Station> stationMap = stationDao.findAllByLineId(lineId).stream()
                .collect(Collectors.toMap(Station::getId, Function.identity()));

        final ConnectedSections connectedSections = new ConnectedSections(sectionDao.findAllByLineId(lineId));
        final List<Station> stations = connectedSections.getSortedStationIds().stream()
                .map(stationMap::get)
                .collect(Collectors.toList());

        return LineResponse.of(lineDao.findById(lineId), stations);
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

    private List<Line> findLines() {
        return lineDao.findAll();
    }
}
