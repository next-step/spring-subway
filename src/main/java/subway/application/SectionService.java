package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.application.dto.SectionParam;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;
import subway.ui.dto.SectionRequest;
import subway.ui.dto.SectionResponse;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public SectionResponse saveSection(final long lineId, final SectionRequest sectionRequest) {

        final Station upStation = getStationById(sectionRequest.getUpStationId());
        final Station downStation = getStationById(sectionRequest.getDownStationId());

        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        final SectionParam params = new SectionParam(lineId, upStation, downStation,
            sectionRequest.getDistance());

        if (sections.isOverlapped(params)) {
            updateOverlappedSection(params, sections);
        }

        final Section newSection = params.to(sections.getLine());
        return SectionResponse.of(sectionDao.insert(newSection));
    }

    public void deleteSection(final long lineId, final long stationId) {
        validateLineInStation(lineId, stationId);
        validateSectionInLine(lineId);

        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));

        if (sections.isLastStation(stationId)) {
            deleteLastSection(stationId, sections);
            return;
        }

        deleteInnerSection(stationId, sections);
    }

    private void updateOverlappedSection(final SectionParam params, final Sections sections) {
        Section updateResult = sections.updateOverlappedSection(params);
        sectionDao.update(updateResult);
    }

    private void deleteInnerSection(long stationId, Sections sections) {
        final Section upDirection = sections.findUpDirectionSection(stationId);
        final Section downDirection = sections.findDownDirectionSection(stationId);
        final Section extendedSection = downDirection.extendToUpDirection(upDirection);

        sectionDao.delete(upDirection.getId());
        sectionDao.update(extendedSection);
    }

    private void deleteLastSection(long stationId, Sections sections) {
        final Section connectedSection = sections.getLastSection(stationId);
        sectionDao.delete(connectedSection.getId());
    }

    private Station getStationById(final long stationId) {
        return stationDao.findById(stationId)
            .orElseThrow(() -> new IllegalStationsException("존재하지 않는 역 정보입니다."));
    }

    private void validateLineInStation(final long lineId, final long stationId) {
        if (!sectionDao.existByLineIdAndStationId(lineId, stationId)) {
            throw new IllegalSectionException("해당 역은 노선에 존재하지 않습니다.");
        }
    }

    private void validateSectionInLine(final long lineId) {
        final long sectionCount = sectionDao.count(lineId);
        if (sectionCount == 1L) {
            throw new IllegalSectionException("해당 노선은 구간이 한개입니다.");
        }
    }
}
