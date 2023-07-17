package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Section;
import subway.domain.Station;

@Service
public class SectionService {

    private final StationDao stationDao;
    private final SectionDao sectionDao;

    SectionService(StationDao stationDao, SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public void saveByStationId(Long upStationId, Long downStationId) {
        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);

        Section section = new Section(upStation, downStation);

        sectionDao.insert(section);
    }
}
