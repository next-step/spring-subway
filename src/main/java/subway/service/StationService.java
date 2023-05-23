package subway.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import subway.domain.entity.Station;
import subway.domain.repository.StationRepository;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station saveStation(Station station) {
        return stationRepository.insert(new Station(station.getName()));
    }

    public Station findStationById(Long id) {
        return stationRepository.findById(id);
    }

    public List<Station> findAllStations() {
        return stationRepository.findAll();
    }

    public void updateStation(Long id, Station newStation) {
        stationRepository.update(new Station(id, newStation.getName()));
    }

    public void deleteStationById(Long id) {
        try {
            stationRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("구간에 등록된 역은 삭제할 수 없습니다. 구간 삭제 후 다시 시도해주세요.");
        }
    }
}
