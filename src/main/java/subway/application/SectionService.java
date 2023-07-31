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
import subway.exception.ErrorCode;
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
                .orElseThrow(() -> new SubwayException(ErrorCode.LINE_ID_NO_EXIST, lineId));
        Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(() -> new SubwayException(ErrorCode.UP_STATION_ID_NO_EXIST, request.getUpStationId()));
        Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(() -> new SubwayException(ErrorCode.DOWN_STATION_ID_NO_EXIST, request.getDownStationId()));

        Section section = new Section(upStation, downStation, request.getDistance());

        Line newLine = line.addSection(section);
        SectionsChange changes = SectionsChange.of(line, newLine);

        sectionDao.delete(changes.getDeletes());
        sectionDao.insert(changes.getInserts(), lineId);
    }

    @Transactional
    public void deleteStation(final Long lineId, final Long stationId) {
        Line line = lineDao.findById(lineId)
                .orElseThrow(() -> new SubwayException(ErrorCode.LINE_ID_NO_EXIST, lineId));
        Station station = stationDao.findById(stationId).orElseThrow();

        Line newLine = line.removeStation(station);
        SectionsChange changes = SectionsChange.of(line, newLine);

        sectionDao.delete(changes.getDeletes());
    }
}
