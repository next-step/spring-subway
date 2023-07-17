package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("추가할 구간의 상행역은 기존 구간들의 하행 종착역과 같아야 한다.")
    void upStationOfNewSectionShouldEqualFinalDownStationOfSections() {
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");
        Section section = new Section(stationA, stationB, 1);
        Section unaddableSection = new Section(stationC, stationD, 1);

        Sections sections = new Sections(List.of(section));

        Assertions.assertThatThrownBy(() -> sections.addLast(unaddableSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("추가할 구간의 하행역은 기존 구간들에 포함되면 안된다.")
    void downStationOfNewSectionShouldNotBeContainedBySections() {
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Section section = new Section(stationA, stationB, 1);
        Section unaddableSection = new Section(stationB, stationA, 1);

        Sections sections = new Sections(List.of(section));

        Assertions.assertThatThrownBy(() -> sections.addLast(unaddableSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 끝에 추가한다.")
    void addLast() {
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Section section = new Section(stationA, stationB, 1);
        Section addableSection = new Section(stationB, stationC, 1);
        Sections sections = new Sections(List.of(section));

        sections.addLast(addableSection);

        assertThat(sections.getLast()).isEqualTo(addableSection);
    }
}
