package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.dto.PathResponse;
import subway.dto.PathRequest;

@RestController
@RequestMapping("/paths")
public class PathController {

    @GetMapping
    public ResponseEntity<PathResponse> getPaths(@ModelAttribute PathRequest pathRequest) {
        PathResponse pathResponse = null;
        return ResponseEntity.ok(pathResponse);
    }

}
