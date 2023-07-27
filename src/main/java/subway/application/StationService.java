package subway.application;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.domain.exception.StatusCodeException;
import subway.dto.StationCreateRequest;
import subway.dto.StationResponse;
import subway.dto.StationUpdateRequest;

@Service
public class StationService {

    private static final String DUPLICATED_STATION = "STATION-SERVICE-401";
    private static final String CANNOT_FIND_STATION = "STATION-SERVICE-402";

    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse saveStation(StationCreateRequest stationCreateRequest) {
        stationDao.findByName(stationCreateRequest.getName())
                .ifPresent(station -> {
                    throw new StatusCodeException(
                            MessageFormat.format("{0}에 해당하는 station이 이미 존재합니다.", stationCreateRequest.getName()),
                            DUPLICATED_STATION);
                });

        Station station = stationDao.insert(new Station(stationCreateRequest.getName()));
        return StationResponse.of(station);
    }

    @Transactional(readOnly = true)
    public StationResponse findStationResponseById(long id) {
        Station station = stationDao.findById(id).orElseThrow(() -> new StatusCodeException(
                MessageFormat.format("station id \"{0}\"에 해당하는 station이 없습니다.", id), CANNOT_FIND_STATION));

        return StationResponse.of(station);
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStationResponses() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStation(long id, StationUpdateRequest stationUpdateRequest) {
        stationDao.update(new Station(id, stationUpdateRequest.getName()));
    }

    @Transactional
    public void deleteStationById(long id) {
        stationDao.deleteById(id);
    }
}