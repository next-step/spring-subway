package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.SectionAdditionRequest;
import subway.dto.SectionResponse;

@Service
public class SectionsService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionsService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public SectionResponse addSection(Long id, SectionAdditionRequest request) {
        Sections sections = sectionDao.findAllByLineId(id);
        Section section = createNewSectionBy(id, request);

        sections.addLast(section);

        return SectionResponse.of(sectionDao.save(section));
    }

    private Section createNewSectionBy(Long id, SectionAdditionRequest request) {
        Line line = getLineOrElseThrow(id);
        Station upStation = getStationOrElseThrow(request.getUpStationId());
        Station downStation = getStationOrElseThrow(request.getDownStationId());
        return new Section(line, upStation, downStation, request.getDistance());
    }

    public void removeLast(Long lineId, Long stationId) {
        Sections sections = sectionDao.findAllByLineId(lineId);
        Station station = getStationOrElseThrow(stationId);

        Section removedSection = sections.removeLast(station);

        sectionDao.deleteById(removedSection.getId());
    }

    private Station getStationOrElseThrow(Long id) {
        return stationDao.findById(id)
            .orElseThrow(
                () -> new RuntimeException("존재하지 않는 station id입니다. id: \"" + id + "\""));
    }

    private Line getLineOrElseThrow(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 station id입니다. id: \"" + id + "\""));
    }
}
