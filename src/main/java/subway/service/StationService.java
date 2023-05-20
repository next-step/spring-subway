package subway.service;

import org.springframework.stereotype.Service;
import subway.domain.repository.StationRepository;
import subway.jdbcdao.StationDao;
import subway.domain.entity.Station;

import java.util.List;
import java.util.NoSuchElementException;

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
        try {
            return stationRepository.findById(id);
        } catch (Exception e) {
            throw new NoSuchElementException("존재하지 않는 역입니다.");
        }
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
