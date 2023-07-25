package subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.StationAlreadyExistException;
import subway.exception.StationNotFoundException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        validateDuplicatedName(stationRequest);
        Station station = stationDao.insert(new Station(stationRequest.getName()));
        return StationResponse.of(station);
    }

    private void validateDuplicatedName(StationRequest stationRequest) {
        if (stationDao.existByName(stationRequest.getName())) {
            throw new StationAlreadyExistException(stationRequest.getName());
        }
    }

    public StationResponse findStationResponseById(Long id) {
        return StationResponse.of(findStationOrThrow(id));
    }

    public List<StationResponse> findAllStationResponses() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStation(Long id, StationRequest stationRequest) {
        findStationOrThrow(id);
        stationDao.update(new Station(id, stationRequest.getName()));
    }

    @Transactional
    public void deleteStationById(Long id) {
        findStationOrThrow(id);
        stationDao.deleteById(id);
    }

    private Station findStationOrThrow(Long id) {
        return stationDao.findById(id).orElseThrow(() -> new StationNotFoundException(id));
    }
}
