package subway.dto;

import subway.domain.Station;

public final class StationResponse {

    private final long id;
    private final String name;

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
