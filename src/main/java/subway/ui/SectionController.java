package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.SectionService;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

import java.net.URI;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long lineId,
            @RequestBody SectionRequest sectionRequest
    ) {
        SectionResponse sectionResponse = sectionService.createSection(lineId, sectionRequest);

        return ResponseEntity.created(URI.create("/lines/" + lineId)).body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long lineId,
            @RequestParam Long stationId
    ) {
        sectionService.deleteSection(lineId, stationId);

        return ResponseEntity.noContent().build();
    }
}
