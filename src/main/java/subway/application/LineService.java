package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.*;
import subway.dto.request.LineCreateRequest;
import subway.dto.request.LineUpdateRequest;
import subway.dto.response.LineCreateResponse;
import subway.dto.response.LineFindResponse;
import subway.exception.SubwayDataAccessException;

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

    @Transactional
    public LineCreateResponse saveLine(final LineCreateRequest request) {
        final Line persistLine = lineDao.insert(new Line(request.getName(), request.getColor()));
        sectionDao.insert(
                new Section(
                        persistLine.getId(),
                        request.getUpStationId(),
                        request.getDownStationId(),
                        new Distance(request.getDistance())
                )
        );

        return LineCreateResponse.of(persistLine);
    }

    @Transactional
    public List<LineFindResponse> findAllLines() {
        final List<Line> lines = lineDao.findAll();

        return lines.stream()
                .map(line -> findLine(line.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public LineFindResponse findLine(final Long lineId) {
        final ConnectedSections connectedSections = new ConnectedSections(sectionDao.findAllByLineId(lineId));
        final List<Station> stations = stationDao.findAllByIds(connectedSections.getConnectedStationIds());

        final Line line = lineDao.findById(lineId)
                .orElseThrow(() -> new SubwayDataAccessException("존재하지 않는 노선입니다. 입력한 식별자: " + lineId));

        return LineFindResponse.of(line, stations);
    }

    @Transactional
    public void updateLine(final Long id, final LineUpdateRequest request) {
        lineDao.update(new Line(id, request.getName(), request.getColor()));
    }

    @Transactional
    public void deleteLine(final Long id) {
        lineDao.deleteById(id);
    }
}
