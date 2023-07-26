package subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.StationPair;
import subway.domain.Stations;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.exception.IllegalLineException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        validateDuplicateName(request.getName());
        final Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));

        sectionDao.insert(new Section(
                persistLine,
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance())
        );

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
        final Line persistLine = findLineById(id);
        final List<StationPair> stationPairs = lineDao.findAllStationPair(id);
        final Stations stations = new Stations(stationPairs);
        return LineResponse.of(persistLine, stations.getStations());
    }

    public Line findLineById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalLineException("노선이 존재하지 않습니다."));
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

    private void validateDuplicateName(final String name) {
        lineDao.findByName(name)
            .ifPresent(line -> {
                throw new IllegalLineException("노선 이름은 중복될 수 없습니다.");
            });
    }

}
