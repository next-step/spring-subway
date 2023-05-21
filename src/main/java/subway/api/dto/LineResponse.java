package subway.api.dto;

import subway.domain.entity.Line;
import subway.domain.entity.Station;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations = null;

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(Long id, String name, String color, List<Station> stations) {
        this(id, name, color);
        this.stations = stations;
    }

    public static LineResponse withStations(Line line, List<Station> stations) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
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

    public List<Station> getStations() {
        return stations;
    }
}
