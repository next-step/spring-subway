package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.application.LineService;
import subway.dto.request.LineCreateRequest;
import subway.dto.request.LineUpdateRequest;
import subway.dto.response.LineResponse;
import subway.dto.response.LineWithStationsResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(final LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody final LineCreateRequest request) {
        final LineResponse line = lineService.createLineAndFirstSection(request);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLines() {
        return ResponseEntity.ok(lineService.findLineResponses());
    }

    @GetMapping("/{lineId}")
    public ResponseEntity<LineWithStationsResponse> findLineById(@PathVariable final Long lineId) {
        return ResponseEntity.ok(lineService.findLineResponseById(lineId));
    }

    @PutMapping("/{lineId}")
    public ResponseEntity<Void> updateLine(@PathVariable final Long lineId,
                                           @RequestBody final LineUpdateRequest lineUpdateRequest) {
        lineService.updateLine(lineId, lineUpdateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{lineId}")
    public ResponseEntity<Void> deleteLine(@PathVariable final Long lineId) {
        lineService.deleteLineById(lineId);
        return ResponseEntity.noContent().build();
    }
}
