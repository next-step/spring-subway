package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.*;
import subway.dto.request.SectionRequest;
import subway.dto.response.SectionResponse;

import java.util.Optional;

@Service
public class SectionService {

    private final StationService stationService;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public SectionService(final StationService stationService,
                          final LineDao lineDao,
                          final SectionDao sectionDao) {
        this.stationService = stationService;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void saveFirstSection(final Long lineId,
                                 final Long upStationId,
                                 final Long downStationId,
                                 final Long distance) {
        sectionDao.insert(createSection(lineId, upStationId, downStationId, distance));
    }

    @Transactional
    public SectionResponse saveSection(final Long lineId,
                                       final SectionRequest request) {
        final Section section = createSection(
                lineId,
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance());
        final Line line = section.getLine();
        throwIfAllOrNothingMatchInLine(section, line);
        addIntersectionIfMatchInLine(section, line);
        final Section persistSection = sectionDao.insert(section);
        return SectionResponse.from(persistSection);
    }

    private void throwIfAllOrNothingMatchInLine(final Section section, final Line line) {
        if (sectionDao.existAllOrNotingInLineBySection(line, section)) {
            throw new IllegalArgumentException("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
        }
    }

    private void addIntersectionIfMatchInLine(final Section section, final Line line) {
        final Optional<Section> optionalSection = sectionDao.findSectionByUpStation(line, section.getUpStation());
        if (optionalSection.isPresent()) {
            final Section originSection = optionalSection.get();
            addIntersectionCaseByUpStation(line, section, originSection);
            deleteOriginSection(originSection);
            return;
        }
        addIntersectionIfDownStationMatchInLine(section, line);
    }

    private void addIntersectionIfDownStationMatchInLine(final Section section, final Line line) {
        sectionDao
                .findSectionByDownStation(line, section.getDownStation())
                .ifPresent((originSection) -> {
                    addIntersectionCaseByDownStation(line, section, originSection);
                    deleteOriginSection(originSection);
                });
    }

    private void addIntersectionCaseByUpStation(final Line line, final Section section, final Section originSection) {
        sectionDao.insert(createSection(line.getId(),
                section.getDownStationId(),
                originSection.getDownStationId(),
                originSection.getDistance() - section.getDistance()));
    }

    private void addIntersectionCaseByDownStation(final Line line, final Section section, final Section originSection) {
        sectionDao.insert(createSection(line.getId(),
                originSection.getUpStationId(),
                section.getUpStationId(),
                originSection.getDistance() - section.getDistance()));
    }

    private void deleteOriginSection(final Section originSection) {
        sectionDao.deleteById(originSection.getId());
    }

    private Section createSection(Long lineId, Long upStationId, Long downStationId, Long distance) {
        final Line line = lineDao.findById(lineId)
                .orElseThrow(() -> new IllegalStateException("노선을 찾을 수 없습니다."));
        final Station upStation = stationService.findStationById(upStationId);
        final Station downStation = stationService.findStationById(downStationId);
        return new Section(line, upStation, downStation, new Distance(distance));
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        final Station station = stationService.findStationById(stationId);
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        validateDeleteConstraint(station, sections);
        final Section lastSection = sections.deleteLastSection();
        sectionDao.deleteById(lastSection.getId());
    }

    private void validateDeleteConstraint(final Station station, final Sections sections) {
        if (!sections.isLastDownStation(station)) {
            throw new IllegalArgumentException("노선에 등록된 하행 종점역만 제거할 수 있습니다.");
        }
    }
}
