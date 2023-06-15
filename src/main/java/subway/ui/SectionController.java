package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.SectionService;
import subway.application.path.PathService;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

import java.net.URI;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;
    private final PathService pathService;

    public SectionController(SectionService sectionService, PathService pathService) {
        this.sectionService = sectionService;
        this.pathService = pathService;
    }

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId, @RequestBody SectionRequest request) {
        SectionResponse response = sectionService.saveSection(lineId, request);
        pathService.addEdge(request.getUpStationId(), request.getDownStationId(), request.getDistance());

        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + response.getId())).body(response);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @PathVariable Long sectionId) {
        SectionResponse sectionResponse = sectionService.findSectionResponseById(sectionId);

        sectionService.deleteSectionById(lineId, sectionId);
        pathService.removeEdge(sectionResponse.getUpStationId(), sectionResponse.getDownStationId());

        return ResponseEntity.noContent().build();
    }

}
