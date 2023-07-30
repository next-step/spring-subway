package subway.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.ShortestPath;
import subway.domain.Station;
import subway.dto.PathRequest;
import subway.dto.PathResponse;
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

        sections.validateAddition(upStation, downStation, distance);
        Section section = new Section(line, upStation, downStation, distance);
        if (sections.isAddedInMiddle(section)) {
            sectionDao.update(sections.findSectionToChange(section));
        }
        sectionDao.save(section);
    }

    @Transactional
    public void remove(Long lineId, Long stationId) {
        Line line = getLineBy(lineId);
        Sections sections = sectionDao.findAllBy(line);
        Station station = getStationBy(stationId);

        sections.validateRemoval(station);
        if (sections.isInMiddle(station)) {
            sectionDao.update(sections.findSectionToChange(station));
        }
        sectionDao.deleteByLineAndStation(line, station);
    }

    @Transactional(readOnly = true)
    public PathResponse findShortestPath(final PathRequest pathRequest) {
        final List<Section> sections = sectionDao.findAll();
        final Station source = getStationBy(pathRequest.getSource());
        final Station target = getStationBy(pathRequest.getTarget());

        final ShortestPath shortestPath = ShortestPath.createDefault(sections, source, target);
        return PathResponse.of(shortestPath);
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
