package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
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

    public LineResponse saveSection(SectionRequest request, Long lineId) {
        Station upStation = stationDao.findById(request.getUpStationId());
        Station downStation = stationDao.findById(request.getDownStationId());
        Section newSection = new Section(upStation, downStation, request.getDistance());

        Sections sections = sectionDao.findAllByLineId(lineId);
        sections.insert(newSection);
        sectionDao.insert(newSection, lineId);

        return findLineResponseById(lineId);
    }

    public void deleteSectionByStationId(Long lineId, String stationId) {
        Long deleteId = Long.parseLong(stationId);
        Station delete = stationDao.findById(deleteId);

        Sections sections = sectionDao.findAllByLineId(lineId);
        sections.delete(delete);
        sectionDao.deleteByStation(delete, lineId);
    }
}
