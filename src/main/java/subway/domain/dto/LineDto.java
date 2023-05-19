package subway.domain.dto;

import lombok.*;
import subway.domain.Line;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class LineDto {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationDto> stations;

    public static LineDto from(Line line) {
        return LineDto.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .stations(line.getStations().stream()
                        .map(StationDto::from).collect(Collectors.toList()))
                .build();
    }
}
