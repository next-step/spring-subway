package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse saveStation(final StationRequest stationRequest) {
        String name = stationRequest.getName();
        validateNotDuplicated(name);
        Station station = stationDao.insert(new Station(name));
        return StationResponse.of(station);
    }

    public StationResponse findStationResponseById(final Long id) {
        return StationResponse.of(stationDao.findById(id));
    }

    public List<StationResponse> findAllStationResponses() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStation(final Long id, final StationRequest stationRequest) {
        stationDao.update(new Station(id, stationRequest.getName()));
    }

    @Transactional
    public void deleteStationById(final Long id) {
        stationDao.deleteById(id);
    }

    private void validateNotDuplicated(String name) {
        stationDao.findByName(name)
                .ifPresent(station -> {
                    throw new IncorrectRequestException(ErrorCode.DUPLICATED_STATION_NAME, name);
                });
    }
}
