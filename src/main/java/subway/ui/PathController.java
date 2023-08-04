package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.application.PathService;
import subway.dto.PathResponse;
import subway.exception.ErrorCode;
import subway.exception.FindPathException;

@RestController
@RequestMapping("/paths")
public class PathController {

    private static final String NUMBER_PATTERN = "-?\\d+";

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(@RequestParam String source, @RequestParam String target) {
        if (!source.matches(NUMBER_PATTERN) || !target.matches(NUMBER_PATTERN)) {
            throw new IllegalArgumentException("출발역과 도착역은 정수만 입력할 수 있습니다.");
        }
        if (source.equals(target)) {
            throw new FindPathException(ErrorCode.SAME_SOURCE_AS_TARGET, "출발역과 도착역이 같습니다.");
        }

        return ResponseEntity.ok(pathService.findShortestPath(Long.parseLong(source), Long.parseLong(target)));
    }
}
