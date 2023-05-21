package subway.service;

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
        stationRepository.deleteById(id);
    }
}
