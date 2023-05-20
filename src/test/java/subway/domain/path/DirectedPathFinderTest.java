package subway.domain.path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.StationStep.강남역;
import static subway.integration.StationStep.미금역;
import static subway.integration.StationStep.정자역;
import static subway.integration.StationStep.판교역;

class DirectedPathFinderTest {

    @DisplayName("노선의 역을 순서대로 출력한다.")
    @Test
    void sortStation() {
        // given
        Station 강남 = new Station(3L, 강남역);
        Station 판교 = new Station(1L, 판교역);
        Station 정자 = new Station(2L, 정자역);
        Station 미금 = new Station(4L, 미금역);

        Section section1 = Section.builder()
                .upStation(판교)
                .downStation(정자)
                .build();
        Section section2 = Section.builder()
                .upStation(강남)
                .downStation(판교)
                .build();
        Section section3 = Section.builder()
                .upStation(정자)
                .downStation(미금)
                .build();

        Sections sections = new Sections(List.of(section1, section2, section3));

        // when
        DirectedPathFinder finder = DirectedPathFinder.of(sections);
        List<Station> path = finder.getPath(강남, 미금);

        // then
        assertThat(path).containsExactly(강남, 판교, 정자, 미금);
    }
}
