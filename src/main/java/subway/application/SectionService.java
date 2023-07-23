package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.*;
import subway.dto.request.SectionRequest;
import subway.dto.response.SectionResponse;

@Service
public class SectionService {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public SectionService(final StationDao stationDao,
                          final LineDao lineDao,
                          final SectionDao sectionDao) {
        this.stationDao = stationDao;
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
        final Section newSection = createSection(
                lineId,
                request.getUpStationId(),
                request.getDownStationId(),
                request.getDistance());
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Sections beforeSections = new Sections(sections.getSections());
        sections.addSection(newSection);
        final Section persistNewSection = sectionDao.dirtyChecking(beforeSections, sections);
        return SectionResponse.from(persistNewSection);
    }

    private Section createSection(Long lineId, Long upStationId, Long downStationId, Long distance) {
        final Line line = getLineById(lineId);
        final Station upStation = getStationById(upStationId);
        final Station downStation = getStationById(downStationId);
        return new Section(line, upStation, downStation, new Distance(distance));
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        final Station station = getStationById(stationId);
        final SortedSections sortedSections = new SortedSections(sectionDao.findAllByLineId(lineId));
        validateDeleteConstraint(station, sortedSections);
        final Section lastSection = sortedSections.deleteLastSection();
        sectionDao.deleteById(lastSection.getId());
    }

    private void validateDeleteConstraint(final Station station, final SortedSections sections) {
        if (!sections.isLastDownStation(station)) {
            throw new IllegalArgumentException("노선에 등록된 하행 종점역만 제거할 수 있습니다.");
        }
    }

    private Station getStationById(final Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalStateException("역 찾을 수 없습니다."));
    }

    private Line getLineById(final Long lineId) {
        return lineDao.findById(lineId)
                .orElseThrow(() -> new IllegalStateException("노선을 찾을 수 없습니다."));
    }
}
