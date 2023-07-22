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
import subway.dto.LineCreateRequest;
import subway.dto.LineResponse;
import subway.dto.LineUpdateRequest;
import subway.dto.SectionCreateRequest;
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

    public LineResponse saveLine(LineCreateRequest request) {
        validLineRequest(request);
        Station upStation = getStation(request.getUpStationId());
        Station downStation = getStation(request.getDownStationId());

        Section section = Section.builder()
                .distance(request.getDistance())
                .upStation(upStation)
                .downStation(downStation)
                .build();

        Line line = lineDao.insert(new Line(request.getName(), request.getColor(), List.of(section)));

        sectionDao.insert(line.getId(), section);
        return LineResponse.from(line, List.of(StationResponse.of(upStation), StationResponse.of(downStation)));
    }

    private void validLineRequest(LineCreateRequest lineCreateRequest) {
        lineDao.findByName(lineCreateRequest.getName()).ifPresent(
                line -> {
                    throw new IllegalArgumentException(
                            MessageFormat.format("{0} 와 일치하는 line 의 이름이 이미 존재합니다.", lineCreateRequest.getName()));
                }
        );
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = findLines();

        return persistLines.stream()
                .map(line -> LineResponse.from(line,
                        stationsToStationResponses(line.getSortedStations())))
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

    public void connectSectionByStationId(Long lineId, SectionCreateRequest sectionCreateRequest) {
        Line line = getLineById(lineId);

        Section newSection = getNewSection(sectionCreateRequest);
        newSection = line.connectSection(newSection);

        sectionDao.insert(line.getId(), newSection);
        updateSectionIfNotNull(newSection.getUpSection());
        updateSectionIfNotNull(newSection.getDownSection());
    }

    private void updateSectionIfNotNull(Section section) {
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
                .orElseThrow(() -> new IllegalArgumentException(
                        MessageFormat.format("lineId \"{0}\"에 해당하는 line이 존재하지 않습니다", id)
                ));
    }

    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

    private Section getNewSection(SectionCreateRequest sectionCreateRequest) {
        Station upStation = getStation(sectionCreateRequest.getUpStationId());
        Station downStation = getStation(sectionCreateRequest.getDownStationId());

        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(sectionCreateRequest.getDistance())
                .build();
    }

    private Station getStation(Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new IllegalArgumentException(
                        MessageFormat.format("stationId \"{0}\"에 해당하는 station이 존재하지 않습니다", stationId)
                )
        );
    }

}
