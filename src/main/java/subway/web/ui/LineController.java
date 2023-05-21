package subway.web.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import subway.domain.application.LineService;
import subway.domain.application.SubwayGraphService;
import subway.domain.dto.AddSectionDto;
import subway.domain.dto.LineDto;
import subway.web.dto.LineRequest;
import subway.web.dto.LineResponse;
import subway.web.dto.SectionRequest;

import javax.validation.Valid;
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

    /**
     * 전체 호선 정보를 조회합니다.
     * @return
     */
    @GetMapping
    public ResponseEntity<List<LineDto>> findAllLines() {
        return ResponseEntity.ok(subwayGraphService.getAllLineWithStations());
    }

    /**
     * 호선 번호로 호선 정보를 조회합니다.
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<LineDto> findLineById(@PathVariable Long id) {
        return ResponseEntity.ok(subwayGraphService.getLineWithStations(id));
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

    /**
     * 그래프 초기화
     * DB의 모든 section 을 조회해와서 그래프를 초기화 시킵니다.
     * @return
     */
    @GetMapping("/sections/init")
    public ResponseEntity<Void> initSections() {
        subwayGraphService.initGraph();
        return ResponseEntity.ok().build();
    }

    /**
     * 해당 호선에 새 구간을 추가합니다.
     * @param id
     * @param sectionRequest
     * @return
     */
    @PostMapping("/{id}/sections")
    public ResponseEntity<Void> addSection(@PathVariable Long id, @RequestBody @Valid SectionRequest sectionRequest) {
        subwayGraphService.addSection(AddSectionDto.builder()
                .lineId(id)
                .downStationId(sectionRequest.getDownStationId())
                .upStationId(sectionRequest.getUpStationId())
                .distance(sectionRequest.getDistance())
                .build());
        return ResponseEntity.ok().build();
    }

    /**
     * 해당 호선의 역을 제거합니다.
     * @param id
     * @param stationId
     * @return
     */
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
