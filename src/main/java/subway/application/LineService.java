package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.request.LineCreateRequest;
import subway.dto.request.LineUpdateRequest;
import subway.dto.response.LineResponse;
import subway.dto.response.LineWithStationsResponse;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class LineService {

    private final LineDao lineDao;

    private final StationService stationService;

    private final SectionService sectionService;

    public LineService(final LineDao lineDao,
                       final StationService stationService,
                       final SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineResponse createLineAndFirstSection(final LineCreateRequest request) {
        final Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));

        sectionService.createFirstSection(
                persistLine.getId(),
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance());

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLines() {
        return lineDao.findAll().stream()
                .map(LineResponse::of)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public LineWithStationsResponse findLineWithStations(final Long lineId) {
        final Line line = findLineById(lineId);
        final List<Station> stations = stationService.findInOrderStationsByLineId(lineId);
        return LineWithStationsResponse.of(line, stations);
    }

    @Transactional
    public void updateLine(final Long lineId, final LineUpdateRequest lineUpdateRequest) {
        final Line line = findLineById(lineId);
        lineDao.update(new Line(line.getId(), lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Transactional
    public void deleteLineById(final Long lineId) {
        final Line line = findLineById(lineId);
        lineDao.deleteById(line.getId());
    }

    private Line findLineById(final Long lineId) {
        return lineDao.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("노선을 찾을 수 없습니다."));
    }

}

