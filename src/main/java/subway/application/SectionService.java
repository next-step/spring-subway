package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
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
        Assert.notNull(upStationId, () -> "upStationId는 null이 될 수 없습니다.");
        Assert.notNull(upStationId, () -> "downStationId는 null이 될 수 없습니다.");

        Station upStation = stationDao.findById(upStationId);
        Station downStation = stationDao.findById(downStationId);

        Section section = Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .build();

        sectionDao.insert(section);
    }
}
