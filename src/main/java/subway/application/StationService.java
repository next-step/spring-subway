package subway.application;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

public interface StationService {

    @Transactional
    StationResponse saveStation(StationRequest stationRequest);

    StationResponse findStationResponseById(Long id);

    List<StationResponse> findAllStationResponses();

    @Transactional
    void updateStation(Long id, StationRequest stationRequest);

    @Transactional
    void deleteStationById(Long id);
}
