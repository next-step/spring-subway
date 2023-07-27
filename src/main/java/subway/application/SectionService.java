package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.SectionAddManager;
import subway.domain.SectionRemoveManager;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.SectionAdditionRequest;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public void addSection(Long id, SectionAdditionRequest request) {
        Line line = getLineBy(id);
        Station upStation = getStationBy(request.getUpStationId());
        Station downStation = getStationBy(request.getDownStationId());
        Sections sections = sectionDao.findAllBy(line);
        int distance = request.getDistance();

        SectionAddManager sectionAddManager = new SectionAddManager(sections);
        sectionAddManager.validate(upStation, downStation, distance);
        Section section = new Section(line, upStation, downStation, distance);
        sectionAddManager.lookForChange(section)
            .ifPresent(sectionDao::update);
        sectionDao.save(section);
    }

    @Transactional
    public void remove(Long lineId, Long stationId) {
        Line line = getLineBy(lineId);
        Sections sections = sectionDao.findAllBy(line);
        Station station = getStationBy(stationId);

        SectionRemoveManager sectionRemoveManager = new SectionRemoveManager(sections);
        sectionRemoveManager.validate(station);
        sectionRemoveManager.lookForChange(station)
            .ifPresent(sectionDao::update);
        sectionDao.deleteByLineAndStation(line, station);
    }

    private Line getLineBy(Long id) {
        return lineDao.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 line id입니다. id: \"" + id + "\""));
    }

    private Station getStationBy(Long id) {
        return stationDao.findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 station id입니다. id: \"" + id + "\""));
    }
}
