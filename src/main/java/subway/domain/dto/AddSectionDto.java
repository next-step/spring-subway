package subway.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AddSectionDto {
    private final Long lineId;
    private final Long downStationId;
    private final Long upStationId;
    private final Integer distance;
}
