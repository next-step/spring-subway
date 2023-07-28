package subway.dto.response;

import subway.domain.Line;

import java.util.List;

public class CreateLineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public CreateLineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static CreateLineResponse of(Line line) {
        return from(line, List.of());
    }

    public static CreateLineResponse from(Line line, List<StationResponse> stations) {
        return new CreateLineResponse(line.getId(), line.getName(), line.getColor(), stations);
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
