package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import subway.dto.PathResponse;

@RestController
@RequestMapping("/paths")
public class PathController {

    private static final String NUMBER_PATTERN = "-?\\d+";

    @GetMapping
    public ResponseEntity<PathResponse> searchPath(@RequestParam String source, @RequestParam String target) {
        if (!source.matches(NUMBER_PATTERN) || !target.matches(NUMBER_PATTERN)) {
            throw new IllegalArgumentException("출발역과 도착역은 정수만 입력할 수 있습니다.");
        }

        return ResponseEntity.ok().build();
    }
}
