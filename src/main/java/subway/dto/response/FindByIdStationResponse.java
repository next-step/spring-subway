package subway.dto.response;

import subway.domain.Station;

public class FindByIdStationResponse {
    private Long id;
    private String name;

    public FindByIdStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static FindByIdStationResponse of(Station station) {
        return new FindByIdStationResponse(station.getId(), station.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
