package subway.api.dto;

import subway.domain.Line;

import javax.validation.constraints.NotBlank;

public class LineRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String color;

    public LineRequest() {
    }

    public LineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toDomain() {
        return new Line(name, color);
    }
}
