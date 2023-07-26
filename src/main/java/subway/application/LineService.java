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

    public LineService(final LineDao lineDao, final SectionDao sectionDao, final StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public LineResponse saveLine(final CreateLineRequest createLineRequest) {
        validateDuplicateName(createLineRequest.getName());

        final Line persistLine = lineDao.insert(new Line(createLineRequest.getName(), createLineRequest.getColor()));
        final Station upStation = getStation(createLineRequest.getUpStationId());
        final Station downStation = getStation(createLineRequest.getDownStationId());
        final Section section = Section.builder()
                .distance(createLineRequest.getDistance())
                .upStation(upStation)
                .downStation(downStation)
                .build();

        sectionDao.insert(section, persistLine.getId());
        return LineResponse.from(persistLine, List.of(StationResponse.of(upStation), StationResponse.of(downStation)));
    }

    private void validateDuplicateName(final String name) {
        lineDao.findByName(name)
                .ifPresent(line -> {
                    throw new LineException(
                            MessageFormat.format("line name \"{0}\"에 해당하는 line이 이미 존재합니다.", line.getName())
                    );
                });
    }

    public List<LineResponse> findAllLines() {
        final List<Line> lines = lineDao.findAll();

        return lines.stream()
                .map(line -> LineResponse.from(line,
                        stationsToStationResponses(stationDao.findAllByLineId(line.getId()))))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(final Long id) {
        final Line line = getLineById(id);

        return LineResponse.from(line, stationsToStationResponses(line.getSortedStations()));
    }

    private List<StationResponse> stationsToStationResponses(final List<Station> stations) {
        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void connectSectionByStationId(final Long lineId, final CreateSectionRequest sectionRequest) {
        final Line line = getLineById(lineId);

        final Section section = getNewSection(sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        final Section newSection = line.connectSection(section);

        sectionDao.insert(newSection, line.getId());
        updateSectionIfNotNull(newSection.getUpSection());
        updateSectionIfNotNull(newSection.getDownSection());
    }

    public void updateSectionIfNotNull(final Section section) {
        if (section == null) {
            return;
        }
        sectionDao.update(section);
    }

    public void disconnectDownSectionByStationId(final Long lineId, final Long stationId) {
        final Line line = getLineById(lineId);
        final Station station = getStation(stationId);

        line.disconnectDownSection(station);

        sectionDao.deleteByLineIdAndStationId(lineId, stationId);
    }

    public void disconnectSectionByStationId(final Long lineId, final Long stationId) {
    }

    private Line getLineById(final Long id) {
        return lineDao.findById(id)
                .orElseThrow(() -> new LineException(
                        MessageFormat.format("lineId \"{0}\"에 해당하는 line이 존재하지 않습니다.", id)
                ));
    }

    public void updateLine(final Long id, final UpdateLineRequest updateLineRequest) {
        lineDao.update(new Line(id, updateLineRequest.getName(), updateLineRequest.getColor()));
    }

    public void deleteLineById(final Long id) {
        lineDao.deleteById(id);
    }

    private Section getNewSection(final Long upStationId, final Long downStationId, final Integer distance) {
        final Station upStation = getStation(upStationId);
        final Station downStation = getStation(downStationId);

        return Section.builder()
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

    private Station getStation(final Long stationId) {
        return stationDao.findById(stationId).orElseThrow(() -> new StationException(
                        MessageFormat.format("stationId \"{0}\"에 해당하는 station이 존재하지 않습니다.", stationId)
                )
        );
    }

}
