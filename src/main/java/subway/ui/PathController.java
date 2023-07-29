package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.PathService;
import subway.dto.request.PathFindRequest;
import subway.dto.response.PathFindResponse;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathFindResponse> find(@RequestParam("source") Long source, @RequestParam("target") Long target) {
        PathFindResponse response = pathService.findShortPath(new PathFindRequest(source, target));
        return ResponseEntity.ok(response);
    }
}
