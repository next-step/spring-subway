package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.CreateStationRequest;
import subway.dto.StationResponse;
import subway.dto.UpdateStationRequest;
import subway.exception.StationException;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse saveStation(final CreateStationRequest createStationRequest) {
        validateDuplicateName(createStationRequest.getName());

        final Station station = stationDao.insert(new Station(createStationRequest.getName()));
        return StationResponse.of(station);
    }

    private void validateDuplicateName(final String name) {
        stationDao.findByName(name)
                .ifPresent(station -> {
                    throw new StationException(
                            MessageFormat.format("station name \"{0}\"에 해당하는 station이 이미 존재합니다.", station.getName())
                    );
                });
    }

    public StationResponse findStationById(final Long id) {
        final Station station = stationDao.findById(id).orElseThrow(() -> new StationException(
                MessageFormat.format("station id \"{0}\"에 해당하는 station이 없습니다.", id)));
        
        return StationResponse.of(station);
    }

    public List<StationResponse> findAllStations() {
        final List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void updateStation(final Long id, final UpdateStationRequest updateStationRequest) {
        stationDao.update(new Station(id, updateStationRequest.getName()));
    }

    public void deleteStationById(final Long id) {
        stationDao.deleteById(id);
    }
}