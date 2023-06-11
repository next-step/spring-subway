package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.api.dto.SectionRequest;
import subway.api.dto.SectionResponse;
import subway.domain.service.SectionService;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/lines/{lineId}/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long lineId,
            @RequestBody @Valid SectionRequest sectionRequest
    ) {
        SectionResponse section = SectionResponse.of(sectionService.saveSection(lineId, sectionRequest.toDomain(lineId)));
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections")).body(section);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long lineId,
            @RequestParam Long stationId
    ) {
        sectionService.deleteSectionByStationId(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
