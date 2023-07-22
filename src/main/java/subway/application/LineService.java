package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineWithStations;
import subway.dto.StationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public LineService(LineDao lineDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        sectionDao.insert(
                new Section(persistLine.getId(), request.getUpStationId(), request.getDownStationId(), request.getDistance()));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findLineResponses() {
        List<Line> persistLines = findLines();
        return persistLines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineAndStationsById(Long id) {
        final List<LineWithStations> lineWithStations = findAllById(id);
        final Sections sections = new Sections(sectionDao.findAllByLineId(id));

        final Map<Long, Station> stations = new HashMap<>();
        for (LineWithStations lineWithStation : lineWithStations) {
            final Station upStation = lineWithStation.getUpStation();
            final Station downStation = lineWithStation.getDownStation();
            stations.put(upStation.getId(), upStation);
            stations.put(downStation.getId(), downStation);
        }

        final List<StationResponse> stationList = new ArrayList<>();
        final List<Long> sortedStationIds = sections.getSortedStationIds();
        for (Long stationId : sortedStationIds) {
            stationList.add(StationResponse.of(stations.get(stationId)));
        }

        final Line line = lineWithStations.get(0).getLine();
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stationList);
    }

    public List<LineWithStations> findAllById(Long id) {
        return lineDao.findAllById(id);
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new Line(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

    private List<Line> findLines() {
        return lineDao.findAll();
    }
}
