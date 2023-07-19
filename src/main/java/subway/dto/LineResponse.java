package subway.dto;

import java.util.List;
import java.util.Objects;
import subway.domain.Line;

public class LineResponse {
    private final String id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineResponse(String id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return from(line, List.of());
    }

    public static LineResponse from(Line line, List<StationResponse> stations) {
        return new LineResponse(String.valueOf(line.getId()), line.getName(), line.getColor(), stations);
    }

    public String getId() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineResponse)) {
            return false;
        }
        LineResponse that = (LineResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
                && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "LineResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
