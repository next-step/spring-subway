package subway.dto.response;

import subway.domain.Station;

public class FindAllStationResponse {
    private Long id;
    private String name;

    public FindAllStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static FindAllStationResponse of(Station station) {
        return new FindAllStationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
