package subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.request.LineRequest;
import subway.dto.response.LineResponse;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        Station upStation = stationDao.findById(request.getUpStationId())
            .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
        Station downStation = stationDao.findById(request.getDownStationId())
            .orElseThrow(() -> new IllegalArgumentException("해당 역이 존재하지 않습니다."));
        Section section = new Section(
            upStation,
            downStation,
            persistLine,
            request.getDistance()
        );
        sectionDao.insert(section);
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

    public LineResponse findLineResponseById(Long id) {
        Line persistLine = findLineById(id);
        Sections sections = sectionDao.findAllByLineId(id);
        List<Station> sortedStations = sections.sortStations();
        return LineResponse.of(persistLine, sortedStations);
    }

    public Line findLineById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 노선이 존재하지 않습니다."));
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Transactional
    public void deleteLineById(Long id) {
        sectionDao.deleteByLineId(id);
        lineDao.deleteById(id);
    }

}
