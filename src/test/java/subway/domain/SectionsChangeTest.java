package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsChangeTest {
    @Test
    @DisplayName("변경사항 DTO 생성 테스트")
    void createSectionsChange() {
        // given
        Section section1 = new Section(
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Section section2 = new Section(
                new Station("신대방역"),
                new Station("상도역"),
                5
        );
        Section section3 = new Section(
                new Station("상도역"),
                new Station("잠실역"),
                10
        );

        Line line1= new Line("2호선", "노랑", new Sections(List.of(section1, section2)));
        Line line2= new Line("2호선", "노랑",  new Sections(List.of(section2, section3)));

        // when
        SectionsChange sectionsChange = SectionsChange.of(line1, line2);

        // then
        assertAll(
                () -> assertThat(sectionsChange.getInserts()).contains(section3),
                () -> assertThat(sectionsChange.getDeletes()).contains(section1)
        );
    }
}
