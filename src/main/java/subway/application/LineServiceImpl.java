package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineWithStationsResponse;
import subway.dto.SectionRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineServiceImpl implements LineService {

    private final LineDao lineDao;

    private final StationService stationService;

    private final SectionServiceImpl sectionServiceImpl;

    public LineServiceImpl(LineDao lineDao, StationService stationService, SectionServiceImpl sectionServiceImpl) {
        this.lineDao = lineDao;
        this.stationService = stationService;
        this.sectionServiceImpl = sectionServiceImpl;
    }

    @Override
    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor())).orElseThrow();
        sectionServiceImpl.saveSection(persistLine.getId(),
                new SectionRequest(request.getUpStationId(), request.getDownStationId(),
                        request.getDistance()));
        return LineResponse.of(persistLine);
    }

    @Override
    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = findLines();
        return persistLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<Line> findLines() {
        return lineDao.findAll();
    }

    @Override
    public LineWithStationsResponse findLineResponseById(Long id) {
        Line persistLine = findLineById(id);
        List<Station> stations = stationService.findStationByLineId(id);
        return LineWithStationsResponse.of(persistLine, stations);
    }

    @Override
    public Line findLineById(Long id) {
        return lineDao.findById(id).orElseThrow();
    }

    @Override
    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Override
    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

}
