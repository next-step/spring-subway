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
    private final Station upStation;
    private final Station downStation;
    private final Distance distance;


    public static Section of(Line line, Station upStation, Station nextStation, Integer distance) {
        return Section.builder()
                .line(line)
                .upStation(upStation)
                .downStation(nextStation)
                .distance(new Distance(distance))
                .build();
    }

    @Override
    public String toString() {
        return "Section{" +
                "line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
