package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.LineSections;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.request.LineRequest;
import subway.dto.request.LineUpdateRequest;
import subway.dto.response.LineResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
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
        Station upStation = getStationOrElseThrow(request.getUpStationId());
        Station downStation = getStationOrElseThrow(request.getDownStationId());
        Section persistSection = sectionDao.save(
            new Section(persistLine, upStation, downStation, request.getDistance()));

        new LineSections(persistLine, persistSection);

        return LineResponse.of(persistLine);
    }

    @Transactional(readOnly = true)
    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = findLines();
        return persistLines.stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Line> findLines() {
        return lineDao.findAll();
    }

    @Transactional(readOnly = true)
    public LineResponse findLineResponseById(Long id) {
        LineSections lineSections = sectionDao.findAllByLine(getLineOrElseThrow(id));
        return LineResponse.of(lineSections);
    }

    private Station getStationOrElseThrow(Long id) {
        return stationDao.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 station id입니다. id: \"" + id + "\""));
    }

    private Line getLineOrElseThrow(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 line id입니다. id: \"" + id + "\""));
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

}
