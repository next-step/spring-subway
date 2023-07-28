package subway.ui;

import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.application.SectionService;
import subway.domain.ShortestPath;
import subway.dto.PathRequest;
import subway.dto.PathResponse;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final SectionService sectionService;

    public PathController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findPath(@ModelAttribute @Valid PathRequest pathRequest) {
        final PathResponse pathResponse = sectionService.findShortestPath(pathRequest);
        return ResponseEntity.ok().body(pathResponse);
    }
}
