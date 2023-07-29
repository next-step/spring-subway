package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.PathService;
import subway.dto.PathFindResponse;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathFindResponse> findPaths(@RequestParam("source") long sourceStationId,
            @RequestParam("target") long targetStationId) {
        return ResponseEntity.ok(pathService.getMinimumPath(sourceStationId, targetStationId));
    }

}
