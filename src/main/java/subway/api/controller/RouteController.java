package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.api.dto.RouteRequest;
import subway.api.dto.RouteResponse;

import javax.validation.Valid;

@RestController
@RequestMapping("/route")
public class RouteController {

    @GetMapping
    public ResponseEntity<RouteResponse> findRoute(
            @RequestBody @Valid RouteRequest routeRequest
    ) {
        // 최단경로 찾기
        // 요금 찾기
        return ResponseEntity.ok().build();
    }
}
