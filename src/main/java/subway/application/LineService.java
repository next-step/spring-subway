package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineWithStationsResponse;
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
    public LineResponse saveLine(LineRequest request) {
        validateName(request.getName());

        final Line line = lineDao.insert(new Line(request.getName(), request.getColor()));
        final Station upStation = findStation(request.getUpStationId());
        final Station downStation = findStation(request.getDownStationId());
        final Section section = new Section(line, upStation, downStation, request.getDistance());

        sectionDao.insert(section);
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

    public LineWithStationsResponse findLineResponseById(Long id) {
        final Line line = findLineById(id);
        final List<Station> stations = new Sections(sectionDao.findAll(id)).getStations();
        return LineWithStationsResponse.of(line, stations);
    }

    public Line findLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new SubwayException(String.format("해당 id(%d)의 노선이 존재하지 않습니다.", id)));
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

    private void validateName(final String name) {
        lineDao.findByName(name)
                .ifPresent(line -> {
                    throw new SubwayException("노선 이름은 중복될 수 없습니다.");
                });
    }

    private Station findStation(final long id) {
        return stationDao.findById(id)
                .orElseThrow(() ->
                        new SubwayException(String.format("해당 id(%d)를 가지는 역이 존재하지 않습니다.", id))
                );
    }
}
