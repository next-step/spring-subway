package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.dto.LineRequest;
import subway.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {
    private final LineDao lineDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        return LineResponse.from(persistLine);
    }
    
    @Transactional(readOnly = true)
    public List<LineResponse> findAll() {
        return lineDao.findAll()
            .stream()
            .map(line -> LineResponse.of(line, stationDao.findAllByLineId(line.getId())))
            .collect(Collectors.toUnmodifiableList());
    }
    @Transactional(readOnly = true)
    public LineResponse findById(Long id) {
        return LineResponse.of(lineDao.findById(id), stationDao.findAllByLineId(id));
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

}
