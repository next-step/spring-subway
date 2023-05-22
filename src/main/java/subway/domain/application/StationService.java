package subway.domain.application;

import org.springframework.stereotype.Service;
import subway.domain.repository.StationRepository;
import subway.persistence.jdbcDao.StationDao;
import subway.domain.Station;
import subway.web.dto.StationRequest;
import subway.web.dto.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationDao stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        validateUniqueName(stationRequest.getName());
        Station station = stationRepository.insert(new Station(stationRequest.getName()));
        return StationResponse.of(station);
    }

    private void validateUniqueName(String name) {
        List<Station> stations = stationRepository.findAll();
        boolean isDuplicate = stations.stream().anyMatch(station -> station.getName().equals(name));
        if (isDuplicate) {
            throw new IllegalArgumentException("중복된 역 이름은 추가할 수 없습니다.");
        }
    }

    public StationResponse findStationResponseById(Long id) {
        return StationResponse.of(stationRepository.findById(id));
    }

    public List<StationResponse> findAllStationResponses() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void updateStation(Long id, StationRequest stationRequest) {
        stationRepository.update(new Station(id, stationRequest.getName()));
    }

    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }
}
