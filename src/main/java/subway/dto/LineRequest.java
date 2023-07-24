package subway.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

public class LineRequest {

    @NotNull(message = "노선 이름은 null 일 수 없습니다.")
    @NotBlank(message = "노선 이름은 비어있을 수 없습니다.")
    @Size(message = "노선 이름의 길이는 1 ~ 255 사이 입니다.", min = 1, max = 255)
    private String name;

    @NotNull(message = "노선 색깔은 null 일 수 없습니다.")
    @NotBlank(message = "노선 색깔은 비어있을 수 없습니다.")
    @Size(message = "노선 색깔의 길이는 1 ~ 20 사이 입니다.", min = 1, max = 20)
    private String color;

    @NotNull(message = "상행역 아이디는 null 일 수 없습니다.")
    @Positive(message = "상행역 아이디는 양수여야합니다.")
    private Long upStationId;

    @NotNull(message = "하행역 아이디는 null 일 수 없습니다.")
    @Positive(message = "하행역 아이디는 양수여야합니다.")
    private Long downStationId;

    @NotNull(message = "거리는 null 일 수 없습니다.")
    @Positive(message = "거리는 양수여야합니다.")
    private Long distance;

    public LineRequest() {
    }

    public LineRequest(String name, String color, Long upStationId, Long downStationId,
            Long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }

}
