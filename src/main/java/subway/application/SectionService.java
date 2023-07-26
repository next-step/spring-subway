package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.SectionsChange;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.exception.SubwayException;

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

    @Transactional
    public void saveSection(final Long lineId, final SectionRequest request) {
        Line line = lineDao.findById(lineId)
                .orElseThrow(() -> new SubwayException("노선 id가 존재하지 않습니다 : " + lineId));
        Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(() -> new SubwayException("상행역 id가 존재하지 않습니다 : " + request.getUpStationId()));
        Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(() -> new SubwayException("하행역 id가 존재하지 않습니다 : " + request.getDownStationId()));

        Section section = new Section(upStation, downStation, request.getDistance());

        Line newLine = line.addSection(section);
        SectionsChange changes = SectionsChange.of(line, newLine);

        changes.getDeletes().forEach(sectionDao::delete);
        changes.getInserts().forEach(s -> sectionDao.insert(s, lineId));
    }

    @Transactional
    public void deleteStation(final Long lineId, final Long stationId) {
        Line line = lineDao.findById(lineId)
                .orElseThrow(() -> new SubwayException("노선 id가 존재하지 않습니다 : " + lineId));
        Station station = stationDao.findById(stationId).orElseThrow();

        Line newLine = line.removeStation(station);
        SectionsChange changes = SectionsChange.of(line, newLine);

        changes.getDeletes().forEach(sectionDao::delete);
    }
}
