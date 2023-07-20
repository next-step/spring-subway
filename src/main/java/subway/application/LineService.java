package subway.application;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.LineManager;
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
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        Station upStation = getStation(Long.valueOf(request.getUpStationId()));
        Station downStation = getStation(Long.valueOf(request.getDownStationId()));
        Section section = Section.builder()
                .line(persistLine)
                .distance(request.getDistance())
                .upStation(upStation)
                .downStation(downStation)
                .build();

        sectionDao.insert(section);
        return LineResponse.from(persistLine, List.of(StationResponse.of(upStation), StationResponse.of(downStation)));
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
        Line persistLine = getLineById(id);
        List<Station> stations = stationDao.findAllByLineId(persistLine.getId());

        return LineResponse.from(persistLine, stationsToStationResponses(stations));
    }

    private List<StationResponse> stationsToStationResponses(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void connectSectionByStationId(Long lineId, SectionRequest sectionRequest) {
        Line line = getLineById(lineId);
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        LineManager lineManager = new LineManager(line, sections);

        Section newSection = getNewSection(line, Long.valueOf(sectionRequest.getUpStationId()),
                Long.valueOf(sectionRequest.getDownStationId()),
                sectionRequest.getDistance());
        lineManager.connectSection(newSection);

        sectionDao.insert(newSection);
    }

    @Transactional
    public void disconnectSectionByStationId(Long lineId, Long stationId) {
        Line line = getLineById(lineId);
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        Station station = getStation(stationId);

        LineManager lineManager = new LineManager(line, sections);

        lineManager.disconnectDownSection(station);

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

    private Section getNewSection(Line line, Long upStationId, Long downStationId, Integer distance) {
        Station upStation = getStation(upStationId);
        Station downStation = getStation(downStationId);

        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

    private Station getStation(Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new IllegalStateException(
                        MessageFormat.format("stationId \"{0}\"에 해당하는 station이 존재하지 않습니다", stationId)
                )
        );
    }

}
