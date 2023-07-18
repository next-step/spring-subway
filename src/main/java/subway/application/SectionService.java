package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.SectionGroup;
import subway.domain.Station;
import subway.dto.SectionRequest;

@Service
public class SectionService {
    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public void saveSection(Long lineId, SectionRequest request) {
        SectionGroup sections = sectionDao.findAllByLineId(lineId);
        Line line = lineDao.findById(lineId);
        Station upward = stationDao.findById(request.getUpStationId());
        Station downward = stationDao.findById(request.getDownStationId());

        if (!sections.isTerminal(upward) || sections.contains(downward)) {
            throw new IllegalArgumentException("새로운 상행역은 기존의 하행 종점역만 설정 가능합니다.");
        }
        sectionDao.insert(new Section(null, upward, downward, line, request.getDistance()));
    }
}
