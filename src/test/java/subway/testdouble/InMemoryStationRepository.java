package subway.testdouble;

import subway.domain.entity.Station;
import subway.domain.repository.StationRepository;

import java.util.*;

public class InMemoryStationRepository implements StationRepository {
    private final Map<Long, Station> stationMap = new HashMap<>();

    @Override
    public Station insert(Station station) {
        stationMap.put(station.getId(), station);
        return station;
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stationMap.values());
    }

    @Override
    public Station findById(Long id) {
        Station station = stationMap.get(id);
        if (station == null) {
            throw new NoSuchElementException("존재하지 않는 역입니다.");
        }
        return station;
    }

    @Override
    public Station findByName(String name) {
        return stationMap.values()
                .stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 역입니다."));
    }

    @Override
    public void update(Station newStation) {
        stationMap.put(newStation.getId(), newStation);
    }

    @Override
    public void deleteById(Long id) {
        stationMap.remove(id);
    }
}
