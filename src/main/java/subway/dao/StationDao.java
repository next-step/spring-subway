package subway.dao;

import subway.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationDao {
    Optional<Station> insert(Station station);

    List<Station> findAll();

    Optional<Station> findById(Long id);

    void update(Station newStation);

    void deleteById(Long id);
}
