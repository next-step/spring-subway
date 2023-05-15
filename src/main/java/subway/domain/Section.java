package subway.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import subway.domain.vo.Distance;

@Getter
@RequiredArgsConstructor
@Builder
public class Section {
    private final Line line;
    private final Station station;
    private final Station downStation;
    private final Distance distance;


    public static Section of(Line line, Station station, Station nextStation, Integer distance) {
        return Section.builder()
                .line(line)
                .station(station)
                .downStation(nextStation)
                .distance(new Distance(distance))
                .build();
    }

    @Override
    public String toString() {
        return "Section{" +
                "line=" + line +
                ", station=" + station +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
