package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.StationService;
import subway.dto.request.StationCreateRequest;
import subway.dto.request.StationUpdateRequest;
import subway.dto.response.StationCreateResponse;
import subway.dto.response.StationFindResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationCreateResponse> createStation(@RequestBody StationCreateRequest request) {
        final StationCreateResponse station = stationService.saveStation(request);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping
    public ResponseEntity<List<StationFindResponse>> showStations() {
        return ResponseEntity.ok().body(stationService.findAllStation());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationFindResponse> showStation(@PathVariable Long id) {
        return ResponseEntity.ok().body(stationService.findStation(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateStation(@PathVariable Long id, @RequestBody StationUpdateRequest request) {
        stationService.updateStation(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
