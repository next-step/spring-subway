package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationPairDao;
import subway.domain.*;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineWithStationsResponse;
import subway.exception.IllegalLineException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationPairDao stationPairDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationPairDao stationPairDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationPairDao = stationPairDao;
    }

    @Transactional
    public LineResponse saveLine(LineRequest request) {
        validateDuplicateName(request.getName());
        final Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));

        sectionDao.insert(new Section(
                persistLine.getId(),
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance())
        );

        return LineResponse.of(persistLine);
    }

    private void validateDuplicateName(final String name) {
        lineDao.findByName(name)
                .ifPresent(line -> {
                    throw new IllegalLineException("노선 이름은 중복될 수 없습니다.");
                });
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
        final Line persistLine = findLineById(id);
        final List<StationPair> stationPairs = stationPairDao.findAllStationPair(id);
        final Stations stations = new Stations(stationPairs);
        return LineWithStationsResponse.of(persistLine, stations.getStations());
    }

    public Line findLineById(Long id) {
        return lineDao.findById(id);
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

}
