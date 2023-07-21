package subway.application;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.SectionRequest;
import subway.dto.StationResponse;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse saveLine(LineRequest request) {
        Station upStation = getStation(Long.valueOf(request.getUpStationId()));
        Station downStation = getStation(Long.valueOf(request.getDownStationId()));

        Section section = Section.builder()
                .distance(request.getDistance())
                .upStation(upStation)
                .downStation(downStation)
                .build();

        Line line = lineDao.insert(new Line(request.getName(), request.getColor(), List.of(section)));

        sectionDao.insert(line.getId(), section);
        return LineResponse.from(line, List.of(StationResponse.of(upStation), StationResponse.of(downStation)));
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = findLines();

        return persistLines.stream()
                .map(line -> LineResponse.from(line,
                        stationsToStationResponses(stationDao.findAllByLineId(line.getId()))))
                .collect(Collectors.toList());
    }

    private List<Line> findLines() {
        return lineDao.findAll();
    }

    public LineResponse findLineResponseById(Long id) {
        Line line = getLineById(id);

        return LineResponse.from(line, stationsToStationResponses(line.getSortedStations()));
    }

    private List<StationResponse> stationsToStationResponses(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void connectSectionByStationId(Long lineId, SectionRequest sectionRequest) {
        Line line = getLineById(lineId);

        Section newSection = getNewSection(sectionRequest);
        newSection = line.connectSection(newSection);

        sectionDao.insert(line.getId(), newSection);
        updateSectionIfNotNull(newSection.getUpSection());
        updateSectionIfNotNull(newSection.getDownSection());
    }

    public void updateSectionIfNotNull(Section section) {
        if (section == null) {
            return;
        }
        sectionDao.update(section);
    }

    public void disconnectSectionByStationId(Long lineId, Long stationId) {
        Station station = getStation(stationId);

        Line line = getLineById(lineId);
        line.disconnectDownSection(station);

        sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
    }

    private Line getLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        MessageFormat.format("lineId \"{0}\"에 해당하는 line이 존재하지 않습니다", id)
                ));
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

    private Section getNewSection(SectionRequest sectionRequest) {
        Station upStation = getStation(Long.valueOf(sectionRequest.getUpStationId()));
        Station downStation = getStation(Long.valueOf(sectionRequest.getDownStationId()));

        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(sectionRequest.getDistance())
                .build();
    }

    private Station getStation(Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new IllegalStateException(
                        MessageFormat.format("stationId \"{0}\"에 해당하는 station이 존재하지 않습니다", stationId)
                )
        );
    }

}
