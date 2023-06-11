package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.api.dto.StationRequest;
import subway.api.dto.StationResponse;
import subway.domain.entity.Station;
import subway.domain.service.StationService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @PostMapping
    public ResponseEntity<StationResponse> createStation(@RequestBody @Valid StationRequest stationRequest) {
        Station station = stationService.saveStation(stationRequest.toDomain());
        return ResponseEntity.created(URI.create("/stations/" + station.getId())).body(StationResponse.of(station));
    }

    @GetMapping
    public ResponseEntity<List<StationResponse>> showStations() {
        List<StationResponse> stations = stationService.findAllStations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(stations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponse> showStation(@PathVariable Long id) {
        return ResponseEntity.ok().body(StationResponse.of(stationService.findStationById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateStation(
            @PathVariable Long id,
            @RequestBody @Valid StationRequest stationRequest
    ) {
        stationService.updateStation(id, stationRequest.toDomain());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStationById(id);
        return ResponseEntity.noContent().build();
    }
}
