package subway.domain.fixture;

import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

public class SectionFixture {
    public static Section createSection(Long id, Line line, Station upStation, Station downStation) {
        return new Section(id, line, upStation, downStation, new Distance(10));
    }

    public static Section createSection(Line line, Station upStation, Station downStation, long distance) {
        return new Section(line, upStation, downStation, new Distance(distance));
    }

    public static Section createSection(Long id, Line line, Station upStation, Station downStation, long distance) {
        return new Section(id, line, upStation, downStation, new Distance(distance));
    }
}
