package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.request.StationCreateRequest;
import subway.dto.request.StationUpdateRequest;
import subway.dto.response.StationCreateResponse;
import subway.dto.response.StationFindResponse;
import subway.exception.SubwayDataAccessException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationCreateResponse saveStation(StationCreateRequest request) {
        final Station station = stationDao.insert(new Station(request.getName()));
        return StationCreateResponse.of(station);
    }

    @Transactional
    public StationFindResponse findStation(final Long id) {
        final Station station = stationDao.findById(id)
                .orElseThrow(() -> new SubwayDataAccessException("존재하지 않는 역입니다. 입력한 식별자: " + id));

        return StationFindResponse.of(station);
    }

    @Transactional
    public List<StationFindResponse> findAllStation() {
        final List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationFindResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStation(final Long id, final StationUpdateRequest request) {
        stationDao.update(new Station(id, request.getName()));
    }

    @Transactional
    public void deleteStation(Long id) {
        stationDao.deleteById(id);
    }
}
