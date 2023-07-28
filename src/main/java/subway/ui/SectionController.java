package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.SectionService;
import subway.dto.request.PathRequest;
import subway.dto.request.SectionRegisterRequest;
import subway.dto.response.PathResponse;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> registerSection(
        @RequestBody SectionRegisterRequest sectionRegisterRequest,
        @PathVariable Long lineId
    ) {
        sectionService.registerSection(sectionRegisterRequest, lineId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(
        @RequestParam Long stationId,
        @PathVariable Long lineId
    ) {
        sectionService.deleteSection(stationId, lineId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/path")
    public PathResponse findSourceToTargetPath(PathRequest pathRequest) {
        return sectionService.findStationToStationDistance(pathRequest);
    }
}
