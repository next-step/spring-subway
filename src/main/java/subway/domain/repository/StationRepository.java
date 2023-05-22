package subway.domain.repository;


import subway.domain.Station;

import java.util.List;

public interface StationRepository {
    Station insert(Station line);
    void update(Station line);
    List<Station> findAll();
    Station findById(Long id);
    void deleteById(Long id);
}
