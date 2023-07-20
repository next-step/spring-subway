package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.LineSections;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.SectionAdditionRequest;
import subway.dto.SectionResponse;
import subway.vo.SectionAdditionResult;

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
        LineSections lineSections = sectionDao.findAllByLineId(id);
        Section section = createNewSectionBy(id, request);

        SectionAdditionResult sectionAdditionResult = lineSections.add(section);

        sectionAdditionResult.getSectionToRemove().ifPresent(sectionDao::delete);
        sectionAdditionResult.getSectionsToAdd().forEach(sectionDao::save);
        return SectionResponse.of(section);
    }

    private Section createNewSectionBy(Long id, SectionAdditionRequest request) {
        Line line = getLineOrElseThrow(id);
        Station upStation = getStationOrElseThrow(request.getUpStationId());
        Station downStation = getStationOrElseThrow(request.getDownStationId());
        return new Section(line, upStation, downStation, request.getDistance());
    }

    public void removeLast(Long lineId, Long stationId) {
        LineSections lineSections = sectionDao.findAllByLineId(lineId);
        Station station = getStationOrElseThrow(stationId);

        Section removedSection = lineSections.removeLast(station);

        sectionDao.deleteById(removedSection.getId());
    }

    private Station getStationOrElseThrow(Long id) {
        return stationDao.findById(id)
            .orElseThrow(
                () -> new RuntimeException("존재하지 않는 station id입니다. id: \"" + id + "\""));
    }

    private Line getLineOrElseThrow(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 line id입니다. id: \"" + id + "\""));
    }
}
