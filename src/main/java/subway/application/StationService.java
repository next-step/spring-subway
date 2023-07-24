package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.SortedSections;
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
    public StationResponse saveStation(final StationRequest stationRequest) {
        final Station station = stationDao.insert(new Station(stationRequest.getName()));
        return StationResponse.of(station);
    }

    @Transactional(readOnly = true)
    public StationResponse findStationResponseById(final Long stationId) {
        final Station station = findStationById(stationId);
        return StationResponse.of(station);
    }

    public Station findStationById(final Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("역을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStationResponses() {
        return stationDao.findAll().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStation(final Long stationId, final StationRequest stationRequest) {
        stationDao.update(new Station(stationId, stationRequest.getName()));
    }

    @Transactional
    public void deleteStationById(final Long stationId) {
        stationDao.deleteById(stationId);
    }

    @Transactional(readOnly = true)
    public List<Station> findStationsByLineId(final Long lineId) {
        SortedSections sections = new SortedSections(sectionDao.findAllByLineId(lineId));
        return sections.toStations();
    }

}
