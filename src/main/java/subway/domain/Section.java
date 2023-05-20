package subway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import subway.domain.vo.Distance;

import java.util.Objects;

@Getter
@AllArgsConstructor
@Builder
public class Section {
    private Long id;
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

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation, distance);
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
