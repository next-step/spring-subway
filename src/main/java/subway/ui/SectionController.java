package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.SectionServiceImpl;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

import java.net.URI;
import java.sql.SQLException;

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
