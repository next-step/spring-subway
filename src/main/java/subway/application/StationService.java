package subway.application;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.StationCreateRequest;
import subway.dto.StationResponse;
import subway.dto.StationUpdateRequest;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse saveStation(StationCreateRequest stationCreateRequest) {
        stationDao.findByName(stationCreateRequest.getName())
                .ifPresent(station -> {
                    throw new IllegalArgumentException(
                            MessageFormat.format("{0}에 해당하는 station이 이미 존재합니다.", stationCreateRequest.getName()));
                });

        Station station = stationDao.insert(new Station(stationCreateRequest.getName()));
        return StationResponse.of(station);
    }

    public StationResponse findStationResponseById(long id) {
        Station station = stationDao.findById(id).orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("station id \"{0}\"에 해당하는 station이 없습니다.", id)));

        return StationResponse.of(station);
    }

    public List<StationResponse> findAllStationResponses() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void updateStation(long id, StationUpdateRequest stationUpdateRequest) {
        stationDao.update(new Station(id, stationUpdateRequest.getName()));
    }

    public void deleteStationById(long id) {
        stationDao.deleteById(id);
    }
}