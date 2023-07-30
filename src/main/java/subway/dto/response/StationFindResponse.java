package subway.dto.response;

import subway.domain.Station;

public class StationFindResponse {
    
    private final Long id;
    private final String name;

    public StationFindResponse(
            final Long id,
            final String name
    ) {
        this.id = id;
        this.name = name;
    }

    public static StationFindResponse of(Station station) {
        return new StationFindResponse(station.getId(), station.getName().getValue());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
