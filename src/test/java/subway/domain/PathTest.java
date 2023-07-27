package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PathTest {

    @DisplayName("경로를 탐색하여 최단거리를 반환한다.")
    @Test
    void pathFind() {
        // given
        Station gyodae = new Station(1L,"교대역");
        Station gangnam = new Station(2L,"강남역");
        Station yangjae = new Station(3L,"양재역");
        Station southTerminal = new Station(4L,"남부터미널역");

        Sections line2Sections = new Sections(List.of(
                new Section(gyodae, gangnam, 10)
        ));
        Sections sinBundangLineSections = new Sections(List.of(
                new Section(gangnam, yangjae, 10)
        ));
        Sections line3Sections = new Sections(List.of(
                new Section(gyodae, southTerminal, 2),
                new Section(southTerminal, yangjae, 3)
        ));

        List<Sections> allSections = List.of(line2Sections, sinBundangLineSections, line3Sections);

        // when
        Path path = new Path(allSections, gyodae, yangjae);

        // then
        assertThat(path.getPath()).containsExactly(gyodae, southTerminal, yangjae);
        assertThat(path.getDistance()).isEqualTo(new Distance(5));
    }
}
