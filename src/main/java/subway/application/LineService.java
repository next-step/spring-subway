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
        final List<Line> persistLines = lineDao.findAll();
        return persistLines.stream()
                .map(line -> findLine(line.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public LineFindResponse findLine(final Long lineId) {
        final ConnectedSections connectedSections = new ConnectedSections(sectionDao.findAllByLineId(lineId));
        final List<Station> stations = stationDao.findAllByIds(connectedSections.getConnectedStationIds());

        return LineFindResponse.of(lineDao.findById(lineId), stations);
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
