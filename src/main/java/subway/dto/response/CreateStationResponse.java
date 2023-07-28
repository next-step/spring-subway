package subway.dto.response;

import subway.domain.Station;

public class CreateStationResponse {
    private Long id;
    private String name;

    public CreateStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CreateStationResponse of(Station station) {
        return new CreateStationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
