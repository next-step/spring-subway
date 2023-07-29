package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.application.StationService;
import subway.dto.request.CreateStationRequest;
import subway.dto.response.CreateStationResponse;
import subway.dto.request.UpdateStationRequest;
import subway.dto.response.FindStationResponse;

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
    public ResponseEntity<CreateStationResponse> createStation(@RequestBody @Validated CreateStationRequest createStationRequest) {
        CreateStationResponse station = stationService.saveStation(createStationRequest);
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(station);
    }

    @GetMapping
    public ResponseEntity<List<FindStationResponse>> findAllStations() {
        return ResponseEntity.ok().body(stationService.findAllStations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindStationResponse> findStationById(@PathVariable Long id) {
        return ResponseEntity.ok().body(stationService.findStationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateStation(@PathVariable Long id, @RequestBody @Validated UpdateStationRequest updateStationRequest) {
        stationService.updateStation(id, updateStationRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
