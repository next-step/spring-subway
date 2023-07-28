package subway.ui;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.PathService;
import subway.dto.PathResponse;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public PathResponse findShortestPath(
            @RequestParam @NotNull final Long source,
            @RequestParam @NotNull final Long target
    ) {
        return pathService.findShortestPath(source, target);
    }
}
