package subway.domain.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import subway.domain.Station;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class StationDto {
    private final Long id;
    private final String name;

    public static StationDto from (Station station) {
        return StationDto.builder()
                .id(station.getId())
                .name(station.getName())
                .build();
    }
}
