package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.LineService;
import subway.application.SectionsService;
import subway.domain.Section;
import subway.dto.LineRequest;
import subway.dto.LineResponse;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import subway.dto.SectionAddtionRequest;
import subway.dto.SectionResponse;
import subway.dto.StationResponse;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SectionsService sectionsService;

    public LineController(LineService lineService, SectionsService sectionsService) {
        this.lineService = lineService;
        this.sectionsService = sectionsService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        LineResponse line = lineService.saveLine(lineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLines() {
        return ResponseEntity.ok(lineService.findLineResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineResponse> findLineById(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.findLineResponseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineUpdateRequest) {
        lineService.updateLine(id, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<SectionResponse> addSection(@PathVariable Long id, @RequestBody SectionAddtionRequest sectionRequest) {
        SectionResponse section = sectionsService.addSection(id, sectionRequest);
        return ResponseEntity.created(URI.create("/line/" + id + "/sections/" +  section.getId())).body(section);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Void> handleSQLException() {
        return ResponseEntity.badRequest().build();
    }
}
