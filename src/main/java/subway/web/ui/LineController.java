package subway.web.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.application.LineService;
import subway.domain.application.SubwayGraphService;
import subway.domain.dto.AddSectionDto;
import subway.web.dto.LineRequest;
import subway.web.dto.LineResponse;
import subway.web.dto.SectionRequest;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;
    private final SubwayGraphService subwayGraphService;

    public LineController(LineService lineService, SubwayGraphService subwayGraphService) {
        this.lineService = lineService;
        this.subwayGraphService = subwayGraphService;
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

    @GetMapping("/sections/init")
    public ResponseEntity<Void> initSections() {
        subwayGraphService.initGraph();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        subwayGraphService.addSection(AddSectionDto.builder()
                .lineId(id)
                .downStationId(sectionRequest.getDownStationId())
                .upStationId(sectionRequest.getUpStationId())
                .distance(sectionRequest.getDistance())
                .build());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSections(@PathVariable Long id, @RequestParam Long stationId) {
        subwayGraphService.removeStation(id ,stationId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Void> handleSQLException() {
        return ResponseEntity.badRequest().build();
    }

}
