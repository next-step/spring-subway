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

    @GetMapping
    public ResponseEntity<PathResponse> searchPath(@RequestParam String source, @RequestParam String target) {
        return ResponseEntity.ok().build();
    }
}
