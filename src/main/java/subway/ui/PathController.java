package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.PathService;
import subway.dto.PathResponse;

@RestController
public class PathController {

    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> findShortestPathWithWeight(
            @RequestParam Long source,
            @RequestParam Long target
    ) {
        final PathResponse shortestPath = pathService.findShortestPath(source, target);

        return ResponseEntity.ok(shortestPath);
    }
}
