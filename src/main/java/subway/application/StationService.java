package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.IllegalStationException;
import subway.exception.IllegalStationsException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse saveStation(StationRequest stationRequest) {
        validateByName(stationRequest.getName());
        Station station = stationDao.insert(new Station(stationRequest.getName()));
        return StationResponse.of(station);
    }

    private void validateByName(final String name) {
        stationDao.findByName(name)
                .ifPresent(station -> {
                    throw new IllegalStationException(String.format("중복된 이름(%s)의 역이 존재합니다.", name));
                });
    }

    public StationResponse findStationById(Long id) {
        return StationResponse.of(findStation(id));
    }

    private Station findStation(final long id) {
        return stationDao.findById(id)
                .orElseThrow(() ->
                        new IllegalStationsException(String.format("해당 id(%d)를 가지는 역이 존재하지 않습니다.", id))
                );
    }

    public List<StationResponse> findAllStations() {
        final List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void updateStation(Long id, StationRequest stationRequest) {
        stationDao.update(new Station(id, stationRequest.getName()));
    }

    public void deleteStationById(Long id) {
        stationDao.deleteById(id);
    }
}
