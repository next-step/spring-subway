package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.*;
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

    @Transactional
    public Line saveLine(final LineRequest request) {
        final Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        sectionDao.insert(
                new Section(
                        persistLine.getId(),
                        request.getUpStationId(),
                        request.getDownStationId(),
                        new Distance(request.getDistance())
                )
        );

        return persistLine;
    }

    @Transactional
    public List<LineResponse> findLineResponses() {
        final List<Line> persistLines = findLines();
        return persistLines.stream()
                .map(line -> findLineResponse(line.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse findLineResponse(final Long lineId) {
        final Map<Long, Station> stationById = stationDao.findAllByLineId(lineId).stream()
                .collect(Collectors.toMap(Station::getId, Function.identity()));

        final ConnectedSections connectedSections = new ConnectedSections(sectionDao.findAllByLineId(lineId));
        final List<Station> stations = connectedSections.getSortedStationIds().stream()
                .map(stationById::get)
                .collect(Collectors.toList());

        return LineResponse.of(lineDao.findById(lineId), stations);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

    private List<Line> findLines() {
        return lineDao.findAll();
    }
}
