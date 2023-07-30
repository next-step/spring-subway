package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.*;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.SubwayException;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final LineDao lineDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public SectionResponse saveSection(final long lineId, final SectionRequest sectionRequest) {
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));

        final Line line = findLine(lineId);
        final Station upStation = findStationById(sectionRequest.getUpStationId());
        final Station downStation = findStationById(sectionRequest.getDownStationId());
        final Section section = new Section(line, upStation, downStation, sectionRequest.getDistance());

        sections.findConnectedSection(section)
                .ifPresent(sectionDao::update);

        return SectionResponse.of(insert(section));
    }

    private Line findLine(final long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new SubwayException(String.format("해당 id(%d)의 노선이 존재하지 않습니다.", id)));
    }

    private Section insert(final Section section) {
        return sectionDao.insert(section);
    }

    @Transactional
    public void deleteSection(final long lineId, final long stationId) {
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        disconnectSection(stationId, sections);
    }

    private void disconnectSection(final long stationId, final Sections sections) {
        final DisconnectedSections disconnectedSections = DisconnectedSections.of(sections.disconnect(stationId));
        if(disconnectedSections.getUpdateSection().isNotNull()) {
            sectionDao.update(disconnectedSections.getUpdateSection());
        }
        sectionDao.delete(disconnectedSections.getDeleteSection());
    }

    private Station findStationById(final long id) {
        return stationDao.findById(id)
                .orElseThrow(() ->
                        new SubwayException(String.format("해당 id(%d)를 가지는 역이 존재하지 않습니다.", id))
                );
    }
}
