package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.SectionService;
import subway.dto.request.SectionCreateRequest;
import subway.dto.response.SectionCreateResponse;

import java.net.URI;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionCreateResponse> createSection(
            @PathVariable Long lineId,
            @RequestBody SectionCreateRequest request
    ) {
        SectionCreateResponse response = sectionService.createSection(lineId, request);

        return ResponseEntity.created(URI.create("/lines/" + lineId)).body(response);
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
