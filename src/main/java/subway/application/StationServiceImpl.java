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

@Service
public class StationServiceImpl implements StationService {

    private final StationDao stationDao;

    public StationServiceImpl(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Override
    public StationResponse saveStation(StationRequest stationRequest) {
        validateDuplicatedName(stationRequest);
        Station station = stationDao.insert(new Station(stationRequest.getName())).orElseThrow();
        return StationResponse.of(station);
    }

    private void validateDuplicatedName(StationRequest stationRequest) {
        if (stationDao.existByName(stationRequest.getName())) {
            throw new StationAlreadyExistException(stationRequest.getName());
        }
    }

    @Override
    public StationResponse findStationResponseById(Long id) {
        return StationResponse.of(stationDao.findById(id).orElseThrow());
    }

    @Override
    public List<StationResponse> findAllStationResponses() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStation(Long id, StationRequest stationRequest) {
        stationDao.update(new Station(id, stationRequest.getName()));
    }

    @Override
    @Transactional
    public void deleteStationById(Long id) {
        stationDao.deleteById(id);
    }
}
