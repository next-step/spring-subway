package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionRemoveManagerTest {

    @Test
    @DisplayName("구간이 1개인 경우 삭제할 수 없다")
    void cannotRemoveOneSizeSections() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");

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
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선에서 중간에 위치한 역을 제거하는 경우 변경 대상 구간이 존재한다.")
    void removeInMiddleThenUpdateExists() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 1);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        SectionRemoveManager sectionRemoveManager = new SectionRemoveManager(sections);

        //when & then
        assertThat(sectionRemoveManager.lookForChange(stationB)).isNotEmpty();
    }

    @Test
    @DisplayName("노선에서 종점역을 제거하는 경우 변경 대상 구간이 존재하지 않는다.")
    void removeEndThenUpdateExists() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 1);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        SectionRemoveManager sectionRemoveManager = new SectionRemoveManager(sections);

        //when & then
        assertThat(sectionRemoveManager.lookForChange(stationA)).isEmpty();
        assertThat(sectionRemoveManager.lookForChange(stationC)).isEmpty();
    }
}
