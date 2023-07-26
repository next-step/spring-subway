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
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineStationsResponse;
import subway.dto.SectionRequest;
import subway.exception.ErrorCode;
import subway.exception.SectionException;
import subway.exception.StationException;

@Service
public class LineService {

    private static final String NO_STATION_EXCEPTION_MESSAGE = "삭제할 역이 존재하지 않습니다.";
    private static final String NO_UP_STATION_EXCEPTION_MESSAGE = "새로운 구간의 상행역이 존재하지 않습니다.";
    private static final String NO_DOWN_STATION_EXCEPTION_MESSAGE = "새로운 구간의 하행역이 존재하지 않습니다.";
    private static final String NO_SAVED_SECTION_EXCEPTION_MESSAGE = "등록된 구간이 없습니다.";
    private static final String NO_SECTIONS_IN_LINE_EXCEPTION_MESSAGE = "해당 노선의 구간이 존재하지 않습니다.";

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
    public LineStationsResponse findLineResponseById(final Long id) {
        Line persistLine = findLineById(id);
        Sections sections = sectionDao.findAllByLineId(id)
                .orElseThrow(() -> new SectionException(ErrorCode.EMPTY_SECTION, NO_SECTIONS_IN_LINE_EXCEPTION_MESSAGE));

        return LineStationsResponse.from(persistLine, sections.toStations());
    }

    @Transactional(readOnly = true)
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
    public Long saveSection(final SectionRequest request, final Long lineId) {
        Section newSection = newSection(request);
        Sections sections = sectionDao.findAllByLineId(lineId)
                .orElseThrow(() -> new SectionException(ErrorCode.EMPTY_SECTION, NO_SECTIONS_IN_LINE_EXCEPTION_MESSAGE));

        sections.validateInsert(newSection);

        if (sections.isInsertedMiddle(newSection)) {
            Section oldSection = sections.oldSection(newSection);
            sectionDao.deleteById(oldSection.getId());
            sectionDao.insert(sections.cut(oldSection, newSection), lineId);
        }
        sectionDao.insert(newSection, lineId);

        final Long newUpStationId = newSection.getUpStation().getId();
        final Long newDownStationId = newSection.getDownStation().getId();

        return sectionDao.findIdByStationIdsAndLineId(newUpStationId, newDownStationId, lineId)
                .orElseThrow(() -> new SectionException(ErrorCode.NO_SUCH_SECTION, NO_SAVED_SECTION_EXCEPTION_MESSAGE));
    }

    @Transactional
    public void deleteSectionByStationId(final Long lineId, final Long stationId) {
        final Station delete = stationDao.findById(stationId)
                .orElseThrow(() -> new StationException(ErrorCode.NO_SUCH_STATION, NO_STATION_EXCEPTION_MESSAGE));
        final Sections sections = sectionDao.findAllByLineId(lineId)
                .orElseThrow(() -> new SectionException(ErrorCode.EMPTY_SECTION, NO_SECTIONS_IN_LINE_EXCEPTION_MESSAGE));

        sections.validateDelete(delete);

        List<Section> removeSections = sections.sectionsForRemoval(delete);
        List<Long> removeIds = removeSections.stream()
                .map(Section::getId)
                .collect(Collectors.toList());

        sectionDao.deleteAllIn(removeIds);

        if (isMidRemoved(removeSections)) {
            sectionDao.insert(newSectionAfterRemoval(removeSections), lineId);
        }
    }

    private boolean isMidRemoved(final List<Section> removeSections) {
        return removeSections.size() == 2;
    }

    private Section newSection(final SectionRequest request) {
        final Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(() -> new StationException(ErrorCode.NO_SUCH_STATION, NO_UP_STATION_EXCEPTION_MESSAGE));
        final Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(() -> new StationException(ErrorCode.NO_SUCH_STATION, NO_DOWN_STATION_EXCEPTION_MESSAGE));

        return new Section(upStation, downStation, request.getDistance());
    }

    private Section newSectionAfterRemoval(final List<Section> removeSections) {
        final Section firstSection = removeSections.get(0);
        final Section secondSection = removeSections.get(1);

        if (firstSection.isInOrder(secondSection)) {
            return new Section(
                    firstSection.getUpStation(), secondSection.getDownStation(), firstSection.distanceSum(secondSection));
        }

        return new Section(
                secondSection.getUpStation(), firstSection.getDownStation(), secondSection.distanceSum(firstSection));
    }
}
