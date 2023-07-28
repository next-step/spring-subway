package subway.ui;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.LineService;
import subway.dto.request.CreateLineRequest;
import subway.dto.response.CreateLineResponse;
import subway.dto.response.FindLineResponse;
import subway.dto.request.UpdateLineRequest;
import subway.dto.request.CreateSectionRequest;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<CreateLineResponse> createLine(@RequestBody @Validated CreateLineRequest createLineRequest) {
        CreateLineResponse line = lineService.saveLine(createLineRequest);
        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<FindLineResponse>> findAllLines() {
        return ResponseEntity.ok(lineService.findAllLines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindLineResponse> findLineById(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.findLineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody @Validated UpdateLineRequest updateLineRequest) {
        lineService.updateLine(id, updateLineRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lineId}/sections")
    public ResponseEntity<Void> createSection(@PathVariable("lineId") Long lineId,
            @RequestBody @Validated CreateSectionRequest createSectionRequest) {

        lineService.connectSectionByStationId(lineId, createSectionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable("lineId") Long lineId,
            @RequestParam("stationId") Long stationId) {

        lineService.disconnectSectionByStationId(lineId, stationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
