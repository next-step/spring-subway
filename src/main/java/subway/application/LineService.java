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
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineWithStationsResponse;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao,
        final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public LineWithStationsResponse saveLine(final LineRequest request) {
        Line line = lineDao.insert(new Line(request.getName(), request.getColor()));
        Station upStation = stationDao.findById(request.getUpStationId());
        Station downStation = stationDao.findById(request.getDownStationId());
        Section section = new Section(upStation, downStation, request.getDistance());

        Line newLine = line.addSections(section);
        sectionDao.insert(section, line.getId());

        return LineWithStationsResponse.of(newLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = lineDao.findAll();
        return persistLines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineWithStationsResponse findLineResponseById(final Long id) {
        Line line = lineDao.findById(id);
        return LineWithStationsResponse.of(line);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Transactional
    public void deleteLineById(final Long id) {
        lineDao.deleteById(id);
    }
}
