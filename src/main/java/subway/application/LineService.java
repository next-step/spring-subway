package subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.LineManager;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;

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

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        Station upStation = stationDao.findById(Long.valueOf(request.getUpStationId()));
        Station downStation = stationDao.findById(Long.valueOf(request.getDownStationId()));
        Section section = Section.builder()
                .line(persistLine)
                .distance(request.getDistance())
                .upStation(upStation)
                .downStation(downStation)
                .build();

        sectionDao.insert(section);
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
        Line persistLine = findLineById(id);
        return LineResponse.of(persistLine);
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

    public void connectSectionByStationId(Long lineId, SectionRequest sectionRequest) {
        Line line = lineDao.findById(lineId);
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        LineManager lineManager = new LineManager(line, sections);

        Section downSection = getDownSection(line, Long.valueOf(sectionRequest.getUpStationId()),
                Long.valueOf(sectionRequest.getDownStationId()),
                sectionRequest.getDistance());
        lineManager.connectDownSection(downSection);

        sectionDao.insert(downSection);
    }

    private Section getDownSection(Line line, Long upStationId, Long downStationId, Integer distance) {
        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);

        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

}
