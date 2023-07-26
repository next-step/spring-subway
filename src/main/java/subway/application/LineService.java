package subway.application;

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
import subway.exception.SubwayException;

import java.util.List;
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
    public LineResponse saveLine(final LineRequest request) {
        Line line = lineDao.insert(new Line(request.getName(), request.getColor()));
        Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(() -> new SubwayException("상행역 id가 존재하지 않습니다 : " + request.getUpStationId()));
        Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(() -> new SubwayException("하행역 id가 존재하지 않습니다 : " + request.getDownStationId()));

        sectionDao.insert(new Section(upStation, downStation, request.getDistance()), line.getId());

        return LineResponse.of(line);
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

    public LineResponse findLineResponseById(final Long id) {
        Line line = findLineById(id);
        return LineResponse.of(line);
    }

    public Line findLineById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new SubwayException("노선 id가 존재하지 않습니다 : " + id));
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
