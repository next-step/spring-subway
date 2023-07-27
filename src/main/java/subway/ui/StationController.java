package subway.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.application.StationService;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.ErrorCode;
import subway.exception.InvalidRequestException;

@RestController
@RequestMapping("/stations")
public class StationController {

    private static final String EMPTY_REQUEST_EXCEPTION_MESSAGE = "비어 있는 요청 정보가 존재합니다.";

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody StationRequest stationRequest) {
        if (stationRequest == null || stationRequest.hasNullField()) {
            throw new InvalidRequestException(ErrorCode.INVALID_REQUEST, EMPTY_REQUEST_EXCEPTION_MESSAGE);
        }

        StationResponse station = stationService.saveStation(stationRequest);

        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStationResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> showStation(@PathVariable Long id) {
        return ResponseEntity.ok().body(stationService.findStationResponseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateStation(@PathVariable Long id, @RequestBody StationRequest stationRequest) {
        if (stationRequest == null || stationRequest.hasNullField()) {
            throw new InvalidRequestException(ErrorCode.INVALID_REQUEST, EMPTY_REQUEST_EXCEPTION_MESSAGE);
        }

        stationService.updateStation(id, stationRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);

        return ResponseEntity.noContent().build();
    }
}
