package subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.LineSections;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.Subway;
import subway.dto.SectionAdditionResult;
import subway.dto.SectionRemovalResult;
import subway.dto.ShortestSubwayPath;
import subway.dto.request.SectionAdditionRequest;

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

    @Transactional
    public void addSection(Long id, SectionAdditionRequest request) {
        Line line = getLineById(id);
        LineSections lineSections = sectionDao.findAllByLine(line);
        Section section = createNewSectionBy(line, request);

        SectionAdditionResult sectionAdditionResult = lineSections.add(section);

        sectionAdditionResult.getSectionToRemove().ifPresent(sectionDao::delete);
        sectionAdditionResult.getSectionsToAdd().forEach(sectionDao::save);
    }

    private Section createNewSectionBy(Line line, SectionAdditionRequest request) {
        Station upStation = getStationById(request.getUpStationId());
        Station downStation = getStationById(request.getDownStationId());
        return new Section(line, upStation, downStation, request.getDistance());
    }

    @Transactional
    public void removeLast(Long lineId, Long stationId) {
        Line line = getLineById(lineId);
        LineSections lineSections = sectionDao.findAllByLine(line);
        Station station = getStationById(stationId);

        SectionRemovalResult sectionRemovalResult = lineSections.remove(station);

        sectionRemovalResult.getSectionToRemove().forEach(sectionDao::delete);
        sectionRemovalResult.getSectionToAdd().ifPresent(sectionDao::save);
    }

    @Transactional
    public ShortestSubwayPath calculateShortestSubwayPath(Long sourceStationId, Long targetStationId) {
        Station sourceStation = getStationById(sourceStationId);
        Station targetStation = getStationById(targetStationId);
        List<Section> allSections = sectionDao.findAll();

        Subway subway = new Subway(allSections);
        return subway.calculateShortestPath(sourceStation, targetStation);
    }

    private Line getLineById(Long id) {
        return lineDao.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 line id입니다. id: \"" + id + "\""));
    }

    private Station getStationById(Long id) {
        return stationDao.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 station id입니다. id: \"" + id + "\""));
    }
}
