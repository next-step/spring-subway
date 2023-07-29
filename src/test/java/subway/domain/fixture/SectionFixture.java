package subway.domain.fixture;

import static subway.domain.fixture.LineFixture.createLineA;
import static subway.domain.fixture.StationFixture.createStationA;
import static subway.domain.fixture.StationFixture.createStationB;
import static subway.domain.fixture.StationFixture.createStationC;
import static subway.domain.fixture.StationFixture.createStationD;

import subway.domain.Line;
import subway.domain.Section;

public class SectionFixture {
    public static Section createSectionA() {
        return new Section(1L, createLineA(), createStationA(), createStationB(), 5);
    }

    public static Section createSectionB() {
        return new Section(1L, createLineA(), createStationB(), createStationC(), 5);
    }

    public static Section createSectionC() {
        return new Section(1L, createLineA(), createStationC(), createStationD(), 5);
    }

    public static Section createSectionC(Line line) {
        return new Section(1L, line, createStationC(), createStationD(), 5);
    }

}
