package subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import subway.domain.Station;

public class StationResponse {

    private Long id;
    private String name;

    private StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public static List<StationResponse> listOf(List<Station> stations) {
        return stations.stream()
            .map(StationResponse::of)
            .collect(Collectors.toUnmodifiableList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
