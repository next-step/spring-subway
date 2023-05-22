package subway.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
public class SectionRequest {
    @NotNull
    private Long downStationId;
    @NotNull
    private Long upStationId;
    @NotNull
    @Min(0)
    private Integer distance;
}
