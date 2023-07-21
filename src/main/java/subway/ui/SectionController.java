package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.SectionService;
import subway.dto.request.SectionRequest;
import subway.dto.response.SectionResponse;

import java.net.URI;
import java.sql.SQLException;

@RestController
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long lineId,
            @RequestBody SectionRequest sectionRequest) {
        SectionResponse sectionResponse = sectionService.saveSection(lineId, sectionRequest);
        return ResponseEntity.created(
                        URI.create("/lines/" + lineId + "/sections/" + sectionResponse.getId()))
                .body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long lineId,
            @RequestParam Long stationId
    ) {
        sectionService.deleteSection(lineId, stationId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Void> handleSQLException(SQLException sqlException) {
        sqlException.printStackTrace();
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException(
            IllegalArgumentException sqlException) {
        sqlException.printStackTrace();
        return ResponseEntity.badRequest().build();
    }
}
