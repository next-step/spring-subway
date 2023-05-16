package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.SectionRepository;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.exception.ErrorType;
import subway.exception.ServiceException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionRepository sectionRepository;

    public LineService(LineDao lineDao, StationDao stationDao, SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = findLines();
        return persistLines.stream()
                .map(it -> findLineResponseById(it.getId()))
                .collect(Collectors.toList());
    }

    public List<Line> findLines() {
        return lineDao.findAll();
    }

    public LineResponse findLineResponseById(long id) {
        Line persistLine = findLineById(id);
        Sections sections = findSectionInLine(persistLine.getId());
        List<Station> stations = findStationInLine(sections);
        return LineResponse.of(persistLine, stations);
    }

    private List<Station> findStationInLine(Sections sections) {
        List<Long> stationIds = sections.getAllStationId();
        return stationDao.findAllByIds(stationIds);
    }

    private Sections findSectionInLine(long lineId) {
        return sectionRepository.findAllByLineId(lineId);
    }

    public Line findLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorType.NOT_EXIST_LINE));
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

}
