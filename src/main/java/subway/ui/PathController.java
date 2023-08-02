package subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.PathService;
import subway.dto.response.PathResponse;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(
        @RequestParam(value = "source") Long departureStationId,
        @RequestParam(value = "target") Long destinationStationId) {

        PathResponse pathResponse = pathService.findShortestPath(departureStationId, destinationStationId);
        return new ResponseEntity<>(pathResponse, HttpStatus.OK);
    }

}
