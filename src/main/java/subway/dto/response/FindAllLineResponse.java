package subway.dto.response;

import subway.domain.Line;

import java.util.List;

public class FindAllLineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public FindAllLineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static FindAllLineResponse of(Line line) {
        return from(line, List.of());
    }

    public static FindAllLineResponse from(Line line, List<StationResponse> stations) {
        return new FindAllLineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
