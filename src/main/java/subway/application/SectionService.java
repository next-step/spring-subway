package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.SectionsChange;
import subway.domain.Station;
import subway.dto.SectionRequest;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public void saveSection(final Long lineId, final SectionRequest request) {
        Line line = lineDao.findById(lineId);
        Station upStation = stationDao.findById(request.getUpStationId());
        Station downStation = stationDao.findById(request.getDownStationId());
        Section section = new Section(upStation, downStation, request.getDistance());

        Line newLine = line.addSection(section);

        SectionsChange changes = SectionsChange.of(line.getSections(), newLine.getSections());
        changes.getDeletes().forEach(sectionDao::delete);
        changes.getInserts().forEach(s -> sectionDao.insert(s, lineId));
    }

    public void deleteStation(Long lineId, Long stationId) {
        Line line = lineDao.findById(lineId);
        Station station = stationDao.findById(stationId);

        line.getSections().remove(station);

        sectionDao.deleteByLineIdAndStationId(lineId, stationId);
    }
}
