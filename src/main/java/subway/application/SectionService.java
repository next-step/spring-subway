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
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;

import java.util.List;

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
        final Sections sections = new Sections(sectionDao.findAll(lineId));

        final Line line = findLine(lineId);
        final Station upStation = findStationById(sectionRequest.getUpStationId());
        final Station downStation = findStationById(sectionRequest.getDownStationId());
        final Section section = new Section(line, upStation, downStation, sectionRequest.getDistance());

        sections.findConnectedSection(section)
                .ifPresent(sectionDao::update);

        return SectionResponse.of(insert(section));
    }

    private Line findLine(final long id) {
        return lineDao.findById(id);
    }

    private Section insert(final Section section) {
        return sectionDao.insert(section);
    }

    @Transactional
    public void deleteSection(final long lineId, final long stationId) {
        final Sections sections = new Sections(sectionDao.findAll(lineId));
        validateSections(sections);

        final List<Section> connectedSections = sections.findConnected(stationId);
        final Section delete = connectedSections.get(0);
        if(connectedSections.size() == 2) {
            final Section update = connectedSections.get(1);
            sectionDao.update(update.updateUpStationAndDistance(delete));
        }

        sectionDao.delete(delete);
    }

    private void validateSections(final Sections sections) {
        if (sections.size() < 2) {
            throw new IllegalSectionException("노선에 구간이 최소 2개가 있어야 삭제가 가능합니다.");
        }
    }

    private Station findStationById(final long id) {
        return stationDao.findById(id)
                .orElseThrow(() ->
                        new IllegalStationsException(String.format("해당 id(%d)를 가지는 역이 존재하지 않습니다.", id))
                );
    }

    private void validateLineAndLastStation(final long lineId, final Station station) {
        final Section lastSection = sectionDao.findLastSection(lineId)
                .orElseThrow(() -> new IllegalSectionException("해당 노선은 생성되지 않았습니다."));

        if (!lastSection.equalsDownStation(station)) {
            throw new IllegalSectionException("해당 역은 노선의 하행 종점역이 아닙니다.");
        }
    }

    private void validateSectionInLine(final long lineId) {
        final long sectionCount = sectionDao.count(lineId);
        if (sectionCount == 1L) {
            throw new IllegalSectionException("해당 노선은 구간이 한개입니다.");
        }
    }
}
