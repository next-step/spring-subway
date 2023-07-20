package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.StationPair;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.StationResponse;
import subway.exception.IllegalLineException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

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

    public LineResponse findLineResponseById(Long id) {
        Line persistLine = findLineById(id);
        List<StationPair> stationPairs = lineDao.findAllStationPair(id);

        final Map<Station, Station> stationMap = stationPairs.stream()
                .collect(Collectors.toMap(StationPair::getUpStation, StationPair::getDownStation));

        // dao 에서 가져온거
        // 생성자로 받고
        // sort() 하면 정렬된 거 방환

        // 상행 종점 구하기
        Set<Station> keySet = new HashSet<>(stationMap.keySet());
        keySet.removeAll(stationMap.values());
        Station lastUpStation = keySet.stream().findAny().orElseThrow();

        List<StationResponse> stationResponses = new ArrayList<>();

        Station curStation = lastUpStation;
        while(curStation != null) {
            stationResponses.add(StationResponse.of(curStation));
            curStation = stationMap.get(curStation);
        }

        return LineResponse.of(persistLine, stationResponses);
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
