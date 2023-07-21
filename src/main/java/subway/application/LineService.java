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
import subway.dto.LineStationsResponse;
import subway.dto.SectionRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        Section section = newSection(SectionRequest.of(request));
        sectionDao.insert(section, persistLine.getId());
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

    @Transactional(readOnly = true)
    public LineStationsResponse findLineResponseById(final Long id) {
        Line persistLine = findLineById(id);
        Sections sections = sectionDao.findAllByLineId(id);
        return LineStationsResponse.from(persistLine, sections.toStations());
    }

    public Line findLineById(final Long id) {
        return lineDao.findById(id);
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Transactional
    public void deleteLineById(final Long id) {
        lineDao.deleteById(id);
    }

    @Transactional
    public void saveSection(final SectionRequest request, final Long lineId) {
        Section newSection = newSection(request);

        Sections sections = sectionDao.findAllByLineId(lineId);
        sections.validateInsert(newSection);

        if (sections.isInsertedMiddle(newSection)) {
            Section oldSection = sections.oldSection(newSection);
            sectionDao.deleteById(oldSection.getId());
            sectionDao.insert(sections.cut(oldSection, newSection), lineId);
        }
        sectionDao.insert(newSection, lineId);
    }

    @Transactional
    public void deleteSectionByStationId(final Long lineId, final String stationId) {
        Long deleteId = Long.parseLong(stationId);
        Station deleteStation = stationDao.findById(deleteId);

        Sections sections = sectionDao.findAllByLineId(lineId);
        sections.validateDelete(deleteStation);
        sectionDao.deleteByStation(deleteStation, lineId);
    }

    private Section newSection(SectionRequest request) {
        Station upStation = stationDao.findById(request.getUpStationId());
        Station downStation = stationDao.findById(request.getDownStationId());
        return new Section(upStation, downStation, request.getDistance());
    }
}
