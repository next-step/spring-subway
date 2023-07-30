package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.fixture.LineFixture;
import subway.domain.fixture.SectionFixture;

@DisplayName("Subway 단위테스트")
class SubwayTest {

    Line lineA;
    Line lineB;

    @BeforeEach
    void setUp() {
        lineA = LineFixture.createLineA();
        lineB = LineFixture.createLineB();
    }

    @Test
    @DisplayName("구간 list로 지하철을 생성한다")
    void createSubway() {

        Section sectionA = SectionFixture.createSectionA();
        Section sectionB = SectionFixture.createSectionB();
        Section sectionC = SectionFixture.createSectionC(lineB);

        Subway subway = new Subway(List.of(sectionA, sectionB, sectionC));

        List<LineSections> expectedLineSections = List.of(
            new LineSections(lineA, new Sections(List.of(sectionA, sectionB))),
            new LineSections(lineB, sectionC));
        assertThat(subway.getLineSections()).isEqualTo(expectedLineSections);
    }
}
