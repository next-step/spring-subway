package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    @DisplayName("새로운 구간을 등록하는 테스트")
    @Test
    void insertNewSection() {
        List<Section> originSections = new ArrayList<>();
        Station upStation = new Station(4L, "잠실역");
        Station downStation = new Station(2L, "잠실나루역");
        Station newDownStation = new Station(1L, "강변역");

        originSections.add(new Section(upStation, downStation,  10));
        Sections sections = new Sections(originSections);

        assertDoesNotThrow(() -> sections.insert(new Section(downStation, newDownStation,  10)));
    }

    @DisplayName("새로운 구간의 상행역이 하행 종점역과 다를 경우 예외로 처리")
    @Test
    void differentStation() {
        Station upStation = new Station(4L, "잠실역");
        Station downStation = new Station(2L, "잠실나루역");
        Station newUpStation = new Station(1L, "강변역");
        Station newDownStation = new Station(3L, "구의역");
        int distance = 10;

        Sections sections = new Sections(List.of(new Section(upStation, downStation, distance)));
        Section  newSection = new Section(newUpStation, newDownStation, distance);

        assertThrows(IllegalArgumentException.class, () -> sections.insert(newSection));
    }

    @DisplayName("새로운 구간의 하행역이 노선에 등록되어 있을 경우")
    @Test
    void alreadyEnrolled() {
        Station upStation = new Station(4L, "잠실역");
        Station downStation = new Station(2L, "잠실나루역");
        Station newUpStation = new Station(2L, "잠실나루역");
        Station newDownStation = new Station(4L, "잠실역");
        int distance = 10;

        Sections sections = new Sections(List.of(new Section(upStation, downStation, distance)));
        Section  newSection = new Section(newUpStation, newDownStation, distance);

        assertThrows(IllegalArgumentException.class, () -> sections.insert(newSection));
    }

    @DisplayName("마지막 구간을 제거하는 경우")
    @Test
    void deleteLastSection() {
        // given
        Station upStation = new Station(4L, "강변역");
        Station downStation = new Station(3L, "구의역");
        Station newUpStation = new Station(3L, "구의역");
        Station newDownStation = new Station(2L, "건대입구역");
        int distance = 10;

        Sections sections = new Sections(List.of(
                new Section(upStation, downStation, distance),
                new Section(newUpStation, newDownStation, distance)));

        // when
        sections.delete(newDownStation);

        // then
        Assertions.assertThat(sections).isEqualTo(new Sections(List.of(new Section(upStation, downStation, distance))));
    }

    @DisplayName("마지막이 아닌 구간을 제거하는 경우 예외를 던진다.")
    @Test
    void deleteNotLastSection() {
        // given
        Station upStation = new Station(4L, "강변역");
        Station downStation = new Station(3L, "구의역");
        Station newUpStation = new Station(3L, "구의역");
        Station newDownStation = new Station(2L, "건대입구역");
        int distance = 10;

        Sections sections = new Sections(List.of(
                new Section(upStation, downStation, distance),
                new Section(newUpStation, newDownStation, distance)));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> sections.delete(downStation));
    }

    @DisplayName("구간이 1개만 있을 때 구간을 제거하는 경우 예외를 던진다.")
    @Test
    void deleteSectionOneAndOnly() {
        // given
        Station upStation = new Station(4L, "강변역");
        Station downStation = new Station(3L, "구의역");
        int distance = 10;

        Sections sections = new Sections(List.of(new Section(upStation, downStation, distance)));

        // when & then
        assertThrows(IllegalStateException.class, () -> sections.delete(downStation));
    }
}
