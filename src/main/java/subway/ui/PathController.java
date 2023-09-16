package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.PathFindService;
import subway.dto.PathResponse;

@RestController
@RequestMapping("paths")
public class PathController {

    private PathFindService pathFindService;

    public PathController(PathFindService pathFindService) {
        this.pathFindService = pathFindService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> getShortPath(@RequestParam Long sourceId, @RequestParam Long targetId) {
        PathResponse response = pathFindService.findShortPath(sourceId, targetId);
        return ResponseEntity.ok().body(response);
    }
}
