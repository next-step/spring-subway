package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.SectionService;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

import java.net.URI;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest request) {
        SectionResponse response = sectionService.saveSection(lineId, request);
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + response.getId())).body(response);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @PathVariable Long sectionId) {
        sectionService.deleteSectionById(lineId, sectionId);
        return ResponseEntity.noContent().build();
    }

}
