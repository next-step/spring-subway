package subway.service;

import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.Station;

import java.util.List;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station saveStation(Station station) {
        return stationDao.insert(new Station(station.getName()));
    }

    public Station findStationById(Long id) {
        return stationDao.findById(id);
    }

    public List<Station> findAllStations() {
        return stationDao.findAll();
    }

    public void updateStation(Long id, Station newStation) {
        stationDao.update(new Station(id, newStation.getName()));
    }

    public void deleteStationById(Long id) {
        stationDao.deleteById(id);
    }
}
