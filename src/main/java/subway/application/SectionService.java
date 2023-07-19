package subway.application;

import java.text.MessageFormat;
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
        validStationIdNotNull(upStationId, downStationId);

        Station upStation = getStation(upStationId);
        Station downStation = getStation(downStationId);

        Section section = buildSection(upStation, downStation);

        sectionDao.insert(section);
    }

    private void validStationIdNotNull(Long upStationId, Long downStationId) {
        Assert.notNull(upStationId, () -> "upStationId는 null이 될 수 없습니다.");
        Assert.notNull(downStationId, () -> "downStationId는 null이 될 수 없습니다.");
    }

    private Station getStation(Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new IllegalStateException(
                        MessageFormat.format("stationId \"{0}\"에 해당하는 station이 존재하지 않습니다", stationId)
                )
        );
    }

    private Section buildSection(Station upStation, Station downStation) {
        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .build();
    }

}
