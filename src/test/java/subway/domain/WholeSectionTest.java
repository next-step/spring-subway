package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.domain.fixture.LineFixture.createLine;
import static subway.domain.fixture.SectionFixture.createSection;
import static subway.domain.fixture.StationFixture.createStation;

class WholeSectionTest {


    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("전체 구간을 담은 WholeSection 에서 전체 역과 구간이 조회되는지 확인한다.")
    void getAllStationInWholeSection() {
        // given
        Line lineA = createLine("2호선");
        Line lineB = createLine("3호선");

        Station stationA = createStation(1L, "낙성대");
        Station stationB = createStation(2L, "사당");
        Station stationC = createStation(3L, "방배");
        Station stationD = createStation(4L, "서초");
        Station stationE = createStation(5L, "교대");

        Section sectionA = createSection(1L, lineA, stationA, stationB);
        Section sectionB = createSection(2L, lineA, stationB, stationC);
        Section sectionC = createSection(3L, lineB, stationB, stationD);
        Section sectionD = createSection(4L, lineB, stationD, stationE);

        List<Section> sections = List.of(sectionA, sectionB, sectionC, sectionD);
        WholeSection wholeSection = new WholeSection(sections);

        // when , then
        assertThat(wholeSection.getAllSections())
                .hasSize(4)
                .contains(sectionA, sectionB, sectionC, sectionD);

        assertThat(wholeSection.getAllStations())
                .hasSize(5)
                .contains(stationA, stationB, stationC, stationD, stationE);
    }
}
