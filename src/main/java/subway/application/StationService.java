package subway.application;

import org.springframework.transaction.annotation.Transactional;
import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

import java.util.List;

public interface StationService {

    @Transactional
    StationResponse saveStation(StationRequest stationRequest);

    StationResponse findStationResponseById(Long id);

    List<StationResponse> findAllStationResponses();

    @Transactional
    void updateStation(Long id, StationRequest stationRequest);

    @Transactional
    void deleteStationById(Long id);

    List<Station> findStationByLineId(Long lineId);
}
