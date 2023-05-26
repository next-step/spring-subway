package subway.web.ui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.domain.application.SubwayGraphService;
import subway.domain.dto.SubwayPathDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/paths")
public class PathController {
    private final SubwayGraphService subwayGraphService;

    @GetMapping()
    public ResponseEntity<SubwayPathDto> getShortenPath(@RequestParam Long startStationId,
                                                        @RequestParam Long endStationId ) {
        return ResponseEntity.ok(subwayGraphService.findShortenPath(startStationId, endStationId));
    }
}
