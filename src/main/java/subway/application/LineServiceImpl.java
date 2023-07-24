package subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineWithStationsResponse;
import subway.exception.LineAlreadyExistException;
import subway.exception.LineNotFoundException;
import subway.exception.StationNotFoundException;

@Service
public class LineServiceImpl implements LineService {

    private final LineDao lineDao;

    private final SectionDao sectionDao;

    private final StationDao stationDao;

    public LineServiceImpl(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Override
    @Transactional
    public LineResponse saveLine(LineRequest request) {
        if (lineDao.existByName(request.getName())) {
            throw new LineAlreadyExistException(request.getName());
        }
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));

        Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException(request.getUpStationId()));
        Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException(request.getDownStationId()));
        Distance distance = new Distance(request.getDistance());
        Section section = new Section(persistLine, upStation, downStation, distance);
        sectionDao.insert(section);

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
        Line persistLine = findLineOrThrow(id);
        List<Section> sectionList = sectionDao.findAllByLineId(id);
        Sections sections = new Sections(sectionList);
        return LineWithStationsResponse.of(persistLine, sections.toStations());
    }

    @Override
    @Transactional
    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        findLineOrThrow(id);
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Override
    @Transactional
    public void deleteLineById(Long id) {
        findLineOrThrow(id);
        lineDao.deleteById(id);
    }

    private Line findLineOrThrow(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new LineNotFoundException(id));
    }
}
