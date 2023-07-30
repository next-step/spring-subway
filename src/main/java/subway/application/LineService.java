package subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.PathDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.DeleteSections;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineStationsResponse;
import subway.dto.SectionRequest;
import subway.dto.StationResponse;
import subway.exception.ErrorCode;
import subway.exception.LineException;
import subway.exception.SectionException;
import subway.exception.StationException;

@Service
public class LineService {

    private static final String NO_STATION_EXCEPTION_MESSAGE = "삭제할 역이 존재하지 않습니다.";
    private static final String NO_UP_STATION_EXCEPTION_MESSAGE = "새로운 구간의 상행역이 존재하지 않습니다.";
    private static final String NO_DOWN_STATION_EXCEPTION_MESSAGE = "새로운 구간의 하행역이 존재하지 않습니다.";
    private static final String NO_SAVED_SECTION_EXCEPTION_MESSAGE = "등록된 구간이 없습니다.";
    private static final String NO_SECTIONS_IN_LINE_EXCEPTION_MESSAGE = "해당 노선의 구간이 존재하지 않습니다.";
    private static final String NO_LINE_EXCEPTION_MESSAGE = "존재하지 않는 노선입니다.";
    private static final String EXISTS_LINE_EXCEPTION_MESSAGE = "이미 존재하는 노선입니다.";

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final PathDao pathDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao,
            final PathDao pathDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.pathDao = pathDao;
    }

    @Transactional
    public LineResponse saveLine(final LineRequest request) {
        Line newLine = new Line(request.getName(), request.getColor());

        if (lineDao.exists(newLine.getLineName())) {
            throw new LineException(ErrorCode.EXISTS_LINE, EXISTS_LINE_EXCEPTION_MESSAGE);
        }

        Line persistLine = lineDao.insert(newLine);
        Section section = newSection(SectionRequest.of(request));

        sectionDao.insert(section, persistLine.getId());
        pathDao.flushCache();

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
                .orElseThrow(
                        () -> new SectionException(ErrorCode.EMPTY_SECTION, NO_SECTIONS_IN_LINE_EXCEPTION_MESSAGE));

        List<StationResponse> stationResponses = sections.toStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return LineStationsResponse.from(persistLine, stationResponses);
    }

    @Transactional(readOnly = true)
    public Line findLineById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new LineException(ErrorCode.NO_SUCH_LINE, NO_LINE_EXCEPTION_MESSAGE));
    }

    @Transactional
    public void updateLine(final Long id, final LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    @Transactional
    public void deleteLineById(final Long id) {
        lineDao.deleteById(id);
        pathDao.flushCache();
    }

    @Transactional
    public Long saveSection(final SectionRequest request, final Long lineId) {
        Section newSection = newSection(request);
        Sections sections = sectionDao.findAllByLineId(lineId)
                .orElseThrow(
                        () -> new SectionException(ErrorCode.EMPTY_SECTION, NO_SECTIONS_IN_LINE_EXCEPTION_MESSAGE));

        sections.validateInsert(newSection);

        if (sections.isInsertedMiddle(newSection)) {
            Section oldSection = sections.oldSection(newSection);
            sectionDao.deleteById(oldSection.getId());
            sectionDao.insert(oldSection.cutBy(newSection), lineId);
        }

        sectionDao.insert(newSection, lineId);
        pathDao.flushCache();

        final Long newUpStationId = newSection.getUpStation().getId();
        final Long newDownStationId = newSection.getDownStation().getId();

        return sectionDao.findIdByStationIdsAndLineId(newUpStationId, newDownStationId, lineId)
                .orElseThrow(() -> new SectionException(ErrorCode.NO_SUCH_SECTION, NO_SAVED_SECTION_EXCEPTION_MESSAGE));
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        final Station delete = stationDao.findById(stationId)
                .orElseThrow(() -> new StationException(ErrorCode.NO_SUCH_STATION, NO_STATION_EXCEPTION_MESSAGE));
        final Sections sections = sectionDao.findAllByLineId(lineId)
                .orElseThrow(
                        () -> new SectionException(ErrorCode.EMPTY_SECTION, NO_SECTIONS_IN_LINE_EXCEPTION_MESSAGE));

        sections.validateDelete();

        DeleteSections deleteSections = new DeleteSections(sections.findSectionsIncluding(delete));

        sectionDao.deleteAllIn(deleteSections.getIds());

        if (deleteSections.isKindOfMidDeletion()) {
            sectionDao.insert(deleteSections.newSection(), lineId);
        }

        pathDao.flushCache();
    }

    private Section newSection(final SectionRequest request) {
        final Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(() -> new StationException(ErrorCode.NO_SUCH_STATION, NO_UP_STATION_EXCEPTION_MESSAGE));
        final Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(() -> new StationException(ErrorCode.NO_SUCH_STATION, NO_DOWN_STATION_EXCEPTION_MESSAGE));

        return new Section(upStation, downStation, request.getDistance());
    }
}
