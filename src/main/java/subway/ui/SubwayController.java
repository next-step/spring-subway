package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.SectionsService;
import subway.dto.ShortestSubwayPath;

@RestController
public class SubwayController {

    private final SectionsService sectionsService;

    public SubwayController(SectionsService sectionsService) {
        this.sectionsService = sectionsService;
    }

    @GetMapping("/path")
    public ResponseEntity<ShortestSubwayPath> calculateShortestSubwayPath(@RequestParam Long source, @RequestParam Long target) {
        ShortestSubwayPath shortestSubwayPath = sectionsService.calculateShortestSubwayPath(source, target);
        return ResponseEntity.ok(shortestSubwayPath);
    }
}
