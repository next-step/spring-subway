package subway.dto.response;

import java.util.List;
import subway.domain.Line;
import subway.domain.LineSections;
import subway.domain.Station;

public class LineResponse {
    private long id;
    private String name;
    private String color;
    private List<StationResponse> stations;


    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public static LineResponse of(LineSections lineSections) {
        Line line = lineSections.getLine();
        List<Station> stations = lineSections.getSections().getStations();

        return new LineResponse(line.getId(), line.getName(), line.getColor(), StationResponse.listOf(stations));
    }

    public long getId() {
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
