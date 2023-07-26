package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.StationService;
import subway.dto.request.StationRequest;
import subway.dto.response.StationResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {

    private final StationService stationService;

    public StationController(final StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(
            @RequestBody final StationRequest stationRequest) {
        final StationResponse station = stationService.createStation(stationRequest);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStations());
    }

    @GetMapping("/{stationId}")
    public ResponseEntity<StationResponse> showStation(@PathVariable final Long stationId) {
        return ResponseEntity.ok().body(stationService.findStation(stationId));
    }

    @PutMapping("/{stationId}")
    public ResponseEntity<Void> updateStation(@PathVariable final Long stationId,
                                              @RequestBody final StationRequest stationRequest) {
        stationService.updateStation(stationId, stationRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{stationId}")
    public ResponseEntity<Void> deleteStation(@PathVariable final Long stationId) {
        stationService.deleteStationById(stationId);
        return ResponseEntity.noContent().build();
    }

}
