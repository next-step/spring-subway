package subway.ui.dto;

import subway.domain.Station;

public class StationResponse {

    private long id;
    private String name;

    private StationResponse(final long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(final long id, final String name) {
        return new StationResponse(id, name);
    }

    public static StationResponse of(final Station station) {
        return new StationResponse(station.getId(), station.getName());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
