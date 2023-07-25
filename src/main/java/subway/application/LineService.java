package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.CreateLineRequest;
import subway.dto.LineResponse;
import subway.dto.CreateSectionRequest;
import subway.dto.StationResponse;
import subway.dto.UpdateLineRequest;
import subway.exception.LineException;
import subway.exception.StationException;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

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

    public LineResponse saveLine(CreateLineRequest createLineRequest) {
        validateDuplicateName(createLineRequest.getName());

        Line persistLine = lineDao.insert(new Line(createLineRequest.getName(), createLineRequest.getColor()));
        Station upStation = getStation(createLineRequest.getUpStationId());
        Station downStation = getStation(createLineRequest.getDownStationId());
        Section section = Section.builder()
                .distance(createLineRequest.getDistance())
                .upStation(upStation)
                .downStation(downStation)
                .build();

        sectionDao.insert(section, persistLine.getId());
        return LineResponse.from(persistLine, List.of(StationResponse.of(upStation), StationResponse.of(downStation)));
    }

    private void validateDuplicateName(String name) {
        lineDao.findByName(name)
                .ifPresent(line -> {
                    throw new LineException(
                            MessageFormat.format("line name \"{0}\"에 해당하는 line이 이미 존재합니다.", line.getName())
                    );
                });
    }

    public List<LineResponse> findAllLines() {
        List<Line> persistLines = findLines();

        return persistLines.stream()
                .map(line -> LineResponse.from(line,
                        stationsToStationResponses(stationDao.findAllByLineId(line.getId()))))
                .collect(Collectors.toList());
    }

    private List<Line> findLines() {
        return lineDao.findAll();
    }

    public LineResponse findLineById(Long id) {
        Line persistLine = getLineById(id);
        List<Section> sections = sectionDao.findAllByLineId(persistLine.getId());
        Line line = new Line(persistLine.getId(),
                            persistLine.getName(),
                            persistLine.getColor(),
                            sections);

        return LineResponse.from(persistLine, stationsToStationResponses(line.getSortedStations()));
    }

    private List<StationResponse> stationsToStationResponses(List<Station> stations) {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void connectSectionByStationId(Long lineId, CreateSectionRequest sectionRequest) {
        Line persistLine = getLineById(lineId);
        List<Section> sections = sectionDao.findAllByLineId(persistLine.getId());

        Line line = toLine(persistLine, sections);

        Section newSection = getNewSection(sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        newSection = line.connectSection(newSection);

        sectionDao.insert(newSection, line.getId());
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
        Line persistLine = getLineById(lineId);
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        Station station = getStation(stationId);
        Line line = toLine(persistLine, sections);

        line.disconnectDownSection(station);

        sectionDao.deleteByLineIdAndDownStationId(lineId, stationId);
    }

    private Line getLineById(Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new LineException(
                        MessageFormat.format("lineId \"{0}\"에 해당하는 line이 존재하지 않습니다.", id)
                ));
    }

    public void updateLine(Long id, UpdateLineRequest updateLineRequest) {
        lineDao.update(new Line(id, updateLineRequest.getName(), updateLineRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

    private Section getNewSection(Long upStationId, Long downStationId, Integer distance) {
        Station upStation = getStation(upStationId);
        Station downStation = getStation(downStationId);

        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

    private Station getStation(Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new StationException(
                        MessageFormat.format("stationId \"{0}\"에 해당하는 station이 존재하지 않습니다.", stationId)
                )
        );
    }

    private Line toLine(Line persistLine, List<Section> sections) {
        return new Line(persistLine.getId(),
                persistLine.getName(),
                persistLine.getColor(),
                sections);
    }

}
