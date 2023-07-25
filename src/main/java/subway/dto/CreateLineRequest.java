package subway.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class CreateLineRequest {

    @NotBlank(message = "노선 이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "노선 색을 선택해주세요.")
    private String color;

    @NotNull(message = "상행역을 선택해주세요.")
    private Long upStationId;

    @NotNull(message = "하행역을 선택해주세요.")
    private Long downStationId;

    @NotNull(message = "거리를 입력해주세요.")
    @Positive(message = "거리는 양수여야합니다.")
    private Integer distance;

    public CreateLineRequest() {
    }

    public CreateLineRequest(String name, String color, Long upStationId, Long downStationId, Integer distance) {
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

    public Integer getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "LineRequest{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", upStationId='" + upStationId + '\'' +
                ", downStationId='" + downStationId + '\'' +
                ", distance=" + distance +
                '}';
    }
}
