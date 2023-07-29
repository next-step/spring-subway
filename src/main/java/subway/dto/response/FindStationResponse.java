package subway.dto.response;

import subway.domain.Station;

public class FindStationResponse {
    private Long id;
    private String name;

    public FindStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static FindStationResponse of(Station station) {
        return new FindStationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
