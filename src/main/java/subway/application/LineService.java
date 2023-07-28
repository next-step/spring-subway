package subway.application;

import static subway.exception.ErrorCode.NOT_FOUND_LINE;
import static subway.exception.ErrorCode.NOT_FOUND_STATION;

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
import subway.exception.SubwayException;

@Service
@Transactional(readOnly = true)
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
        Station upStation = stationDao.findById(request.getUpStationId())
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        Station downStation = stationDao.findById(request.getDownStationId())
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        Section section = new Section(upStation, downStation, request.getDistance());

        Line newLine = line.addSections(section);
        sectionDao.insert(section, line.getId());

        return LineWithStationsResponse.of(newLine);
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = lineDao.findAll();
        return persistLines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineWithStationsResponse findLineResponseById(final Long id) {
        Line line = lineDao.findById(id)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_LINE));
        return LineWithStationsResponse.of(line);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Transactional
    public void deleteLineById(final Long id) {
        Line line = lineDao.findById(id)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_LINE));
        sectionDao.deleteSections(line.getSections().getSections());
        lineDao.deleteById(id);
    }
}
