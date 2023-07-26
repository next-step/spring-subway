package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionRemoveManagerTest {

    @Test
    @DisplayName("구간이 1개인 경우 구간을 삭제할 수 없다")
    void cannotRemoveOneSizeSections() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");
        Station stationE = new Station(5L, "E");

        Section section = new Section(1L, lineA, stationA, stationB, 1);
        Sections sections = new Sections(List.of(section));

        SectionRemoveManager sectionRemoveManager = new SectionRemoveManager(sections);

        //when & then
        assertThatThrownBy(() -> sectionRemoveManager.validate(stationB))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("노선에 등록되어 있지 않은 역을 제거할 수 없다.")
    void cannotRemoveUnregisteredStation() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 2);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        SectionRemoveManager sectionRemoveManager = new SectionRemoveManager(sections);

        //when & then
        assertThatThrownBy(() -> sectionRemoveManager.validate(stationD))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("노선에서 역을 제거할 수 있는 경우 변경 대상 구간이 존재한다.")
    void removeThenUpdateExists() {
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 1);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        SectionRemoveManager sectionRemoveManager = new SectionRemoveManager(sections);

        //when & then
        assertThat(sectionRemoveManager.lookForChange(stationA)).isNotEmpty();
    }
}
