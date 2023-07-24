package subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.SectionService;
import subway.dto.request.SectionRequest;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> createSection(
            @PathVariable final Long lineId,
            @RequestBody final SectionRequest sectionRequest) {
        sectionService.createSection(lineId, sectionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(
            @PathVariable final Long lineId,
            @RequestParam final Long stationId) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
