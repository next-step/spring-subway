package subway.dto;

import subway.domain.Line;

import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
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

    static class LineResponseBuilder {
        private Long id;
        private String name;
        private String color;
        private List<StationResponse> stations;

        public LineResponseBuilder() {
            this.stations = new ArrayList<>();
        }

        public LineResponseBuilder line(Line line) {
            this.id = line.getId();
            this.name = line.getName();
            this.color = line.getColor();
            return this;
        }

        public LineResponseBuilder station(StationResponse station) {
            this.stations.add(station);
            return this;
        }

        public LineResponse build() {
            return new LineResponse(this.id, this.name, this.color, this.stations);
        }
    }

}
