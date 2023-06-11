package subway.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.api.dto.RouteRequest;
import subway.api.dto.RouteResponse;
import subway.domain.service.RouteService;
import subway.domain.vo.Route;

import javax.validation.Valid;

@RestController
@RequestMapping("/route")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<RouteResponse> findRoute(
            @RequestBody @Valid RouteRequest routeRequest
    ) {
        Route shortestRoute = routeService.getShortestRoute(routeRequest.getSourceStationName(), routeRequest.getDestinationStationName());
        return ResponseEntity.ok(RouteResponse.of(shortestRoute));
    }
}
