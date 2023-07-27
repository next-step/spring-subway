package subway.ui;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
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
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineStationsResponse;
import subway.dto.SectionRequest;
import subway.exception.ErrorCode;
import subway.exception.InvalidRequestException;

@RestController
@RequestMapping("/lines")
public class LineController {

    private static final String NOT_POSITIVE_STATION_ID_EXCEPTION_MESSAGE = "stationId는 정수만 입력받을 수 있습니다.";
    private static final String EMPTY_REQUEST_EXCEPTION_MESSAGE = "비어 있는 요청 정보가 존재합니다.";

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody LineRequest lineRequest) {
        if (lineRequest == null || lineRequest.hasCreateNullField()) {
            throw new InvalidRequestException(ErrorCode.INVALID_REQUEST, EMPTY_REQUEST_EXCEPTION_MESSAGE);
        }

        LineResponse line = lineService.saveLine(lineRequest);

        return ResponseEntity.created(URI.create("/lines/" + line.getId())).body(line);
    }

    @GetMapping
    public ResponseEntity<List<LineResponse>> findAllLines() {
        return ResponseEntity.ok(lineService.findLineResponses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineStationsResponse> findLineById(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.findLineResponseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineRequest) {
        if (lineRequest == null || lineRequest.hasUpdateNullField()) {
            throw new InvalidRequestException(ErrorCode.INVALID_REQUEST, EMPTY_REQUEST_EXCEPTION_MESSAGE);
        }

        lineService.updateLine(id, lineRequest);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        lineService.deleteLineById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> createSection(@PathVariable Long id, @RequestBody SectionRequest sectionRequest) {
        if (sectionRequest == null || sectionRequest.hasNullField()) {
            throw new InvalidRequestException(ErrorCode.INVALID_REQUEST, EMPTY_REQUEST_EXCEPTION_MESSAGE);
        }

        Long newSectionId = lineService.saveSection(sectionRequest, id);

        return ResponseEntity.created(URI.create("/lines/" + id + "/sections/" + newSectionId)).build();
    }

    @DeleteMapping("/{id}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id, @RequestParam String stationId) {
        if (!stationId.strip().matches("\\d+")) {
            throw new InvalidRequestException(ErrorCode.INVALID_REQUEST, NOT_POSITIVE_STATION_ID_EXCEPTION_MESSAGE);
        }

        lineService.deleteSection(id, Long.parseLong(stationId));

        return ResponseEntity.noContent().build();
    }
}
