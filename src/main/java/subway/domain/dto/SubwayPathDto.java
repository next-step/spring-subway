package subway.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import subway.domain.vo.SubwayPath;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Builder
public class SubwayPathDto {
    private final List<StationDto> stations;
    private final Double distance;
    private final Integer fare;

    public static SubwayPathDto from(SubwayPath subwayPath) {
        return SubwayPathDto.builder()
                .stations(subwayPath.getStations().stream()
                        .map(StationDto::from)
                        .collect(Collectors.toList()))
                .distance(subwayPath.getDistance())
                .fare(subwayPath.getFare().getValue())
                .build();
    }
}
