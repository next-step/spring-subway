package subway.dto;

import subway.domain.Line;

public class LineDataResponse {
    private Long id;
    private String name;
    private String color;

    public LineDataResponse() {
    }

    public LineDataResponse(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static LineDataResponse of(Line line) {
        return new LineDataResponse(
                line.getId(),
                line.getName(),
                line.getColor()
        );
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

}
