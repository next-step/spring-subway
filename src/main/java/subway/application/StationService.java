package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.SortedLineSections;
import subway.domain.Station;
import subway.dto.request.StationRequest;
import subway.dto.response.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public StationService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse createStation(final StationRequest stationRequest) {
        final Station station = stationDao.insert(new Station(stationRequest.getName()));
        return StationResponse.of(station);
    }

    @Transactional(readOnly = true)
    public StationResponse findStation(final Long stationId) {
        final Station station = findStationById(stationId);
        return StationResponse.of(station);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStations() {
        return stationDao.findAll().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStation(final Long stationId, final StationRequest stationRequest) {
        Station station = findStationById(stationId);
        stationDao.update(new Station(station.getId(), stationRequest.getName()));
    }

    @Transactional
    public void deleteStationById(final Long stationId) {
        Station station = findStationById(stationId);
        stationDao.deleteById(station.getId());
    }

    @Transactional(readOnly = true)
    public List<Station> findInOrderStationsByLineId(final Long lineId) {
        SortedLineSections sections = new SortedLineSections(sectionDao.findAllByLineId(lineId));
        return sections.toStations();
    }

    private Station findStationById(final Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("역을 찾을 수 없습니다."));
    }
}
