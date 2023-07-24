package subway.ui;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.SectionServiceImpl;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

@RestController
public class SectionController {

    private final SectionServiceImpl sectionServiceImpl;

    public SectionController(SectionServiceImpl sectionServiceImpl) {
        this.sectionServiceImpl = sectionServiceImpl;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long lineId,
            @RequestBody SectionRequest sectionRequest) {
        SectionResponse sectionResponse = sectionServiceImpl.saveSection(lineId, sectionRequest);
        return ResponseEntity.created(
                        URI.create("/lines/" + lineId + "/sections/" + sectionResponse.getId()))
                .body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long lineId,
            @RequestParam Long stationId
    ) {
        sectionServiceImpl.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }
}
