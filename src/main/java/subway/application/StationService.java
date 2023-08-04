package subway.application;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.ErrorCode;
import subway.exception.StationException;

@Service
public class StationService {

    private final StationDao stationDao;

    public StationService(final StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse saveStation(final StationRequest stationRequest) {
        Station newStation = new Station(stationRequest.getName());

        if (stationDao.exists(newStation.getStationName())) {
            throw new StationException(ErrorCode.EXISTS_STATION, "이미 존재하는 지하철 역입니다.");
        }

        Station station = stationDao.insert(newStation);

        return StationResponse.of(station);
    }

    @Transactional(readOnly = true)
    public StationResponse findStationResponseById(final Long id) {
        final Station station = stationDao.findById(id).orElseThrow(
                () -> new StationException(ErrorCode.NO_SUCH_STATION, "존재하지 않는 지하철 역입니다."));

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
    public void updateStation(final Long id, final StationRequest stationRequest) {
        stationDao.update(new Station(id, stationRequest.getName()));
    }

    @Transactional
    public void deleteStationById(final Long id) {
        stationDao.findById(id).orElseThrow(
                () -> new StationException(ErrorCode.NO_SUCH_STATION, "존재하는 지하철 역만 삭제할 수 있습니다."));

        stationDao.deleteById(id);
    }
}