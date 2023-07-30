package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Path;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.response.FindPathResponse;
import subway.exception.StationException;

import java.text.MessageFormat;
import java.util.List;

@Service
public class PathService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public PathService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public FindPathResponse findPath(Long source, Long target) {
        return null;
    }

    private Station getStation(final Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new StationException(
                        MessageFormat.format("stationId \"{0}\"에 해당하는 station이 존재하지 않습니다.", stationId)
                )
        );
    }
}
