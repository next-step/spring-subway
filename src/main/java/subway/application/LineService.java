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
import subway.vo.StationPair;
import subway.domain.Stations;
import subway.ui.dto.LineRequest;
import subway.ui.dto.LineResponse;
import subway.exception.IllegalLineException;
import subway.exception.IllegalStationsException;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        validateDuplicateName(request.getName());
        final Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));

        Station upStation = getStationById(request.getUpStationId());
        Station downStation = getStationById(request.getDownStationId());

        sectionDao.insert(new Section(persistLine, upStation, downStation, request.getDistance()));

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
        final List<StationPair> stationPairs = getStationPairs(id);
        final Stations stations = new Stations(stationPairs);
        return LineResponse.of(persistLine, stations.getStations());
    }

    private Station getStationById(long stationId) {
        return stationDao.findById(stationId)
            .orElseThrow(() -> new IllegalStationsException("존재하지 않는 역입니다."));
    }

    private List<StationPair> getStationPairs(Long id) {
        return sectionDao.findAll(id).stream()
            .map(StationPair::of)
            .collect(Collectors.toList());
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
