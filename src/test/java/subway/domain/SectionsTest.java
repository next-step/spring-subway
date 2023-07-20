package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    @DisplayName("하행 종점역을 연장한다.")
    @Test
    void insertDownSection() {
        List<Section> originSections = new ArrayList<>();
        Station upStation = new Station(4L, "잠실역");
        Station downStation = new Station(2L, "잠실나루역");
        Station newDownStation = new Station(1L, "강변역");

        originSections.add(new Section(upStation, downStation,  10));
        Sections sections = new Sections(originSections);

        Section newSection = new Section(downStation, newDownStation, 10);

        assertDoesNotThrow(() -> sections.validateInsert(newSection));
    }

    @DisplayName("상행 종점역을 연장한다.")
    @Test
    void insertUpSection() {
        Station newUpStation = new Station(4L, "잠실역");
        Station upStation = new Station(2L, "잠실나루역");
        Station downStation = new Station(1L, "강변역");

        List<Section> originSections = new ArrayList<>();
        originSections.add(new Section(upStation, downStation,  10));
        Sections sections = new Sections(originSections);

        Section newSection = new Section(newUpStation, upStation, 10);

        assertDoesNotThrow(() -> sections.validateInsert(newSection));
    }

    @DisplayName("기존 구간의 하행역을 기준역으로 중간 역을 추가한다.")
    @Test
    void insertUpSectionInMiddle() {
        Station upStation = new Station(2L, "잠실나루역");
        Station midStation = new Station(4L, "잠실역");
        Station downStation = new Station(1L, "강변역");

        List<Section> originSections = new ArrayList<>();
        originSections.add(new Section(upStation, downStation,  10));
        Sections sections = new Sections(originSections);

        Section newSection = new Section(midStation, downStation, 3);

        assertDoesNotThrow(() -> sections.validateInsert(newSection));
    }

    @DisplayName("기존 구간의 상행역을 기준역으로 중간 역을 추가한다.")
    @Test
    void insertDownSectionInMiddle() {
        Station upStation = new Station(2L, "잠실나루역");
        Station midStation = new Station(4L, "잠실역");
        Station downStation = new Station(1L, "강변역");

        List<Section> originSections = new ArrayList<>();
        originSections.add(new Section(upStation, downStation,  10));
        Sections sections = new Sections(originSections);

        Section newSection = new Section(upStation, midStation, 3);

        assertDoesNotThrow(() -> sections.validateInsert(newSection));
    }

    @DisplayName("새로운 구간을 삽입할 때 두 역이 모두 존재한다면 예외를 던진다.")
    @Test
    void validateInsertAllExist() {
        Station upStation = new Station(4L, "잠실나루역");
        Station downStation = new Station(2L, "잠실역");

        List<Section> originSections = new ArrayList<>();
        originSections.add(new Section(upStation, downStation,  10));
        Sections sections = new Sections(originSections);
        Section newSection = new Section(upStation, downStation,  3);

        assertThrows(IllegalArgumentException.class, () -> sections.validateInsert(newSection));
    }

    @DisplayName("새로운 구간을 삽입할 때 두 역이 모두 존재하지 않으면 예외를 던진다.")
    @Test
    void validateInsertAllNotExist() {
        Station upStation = new Station(4L, "잠실나루역");
        Station downStation = new Station(2L, "잠실역");
        Station newUpStation = new Station(1L, "강변역");
        Station newDownStation = new Station(3L, "구의역");

        List<Section> originSections = new ArrayList<>();
        originSections.add(new Section(upStation, downStation,  10));
        Sections sections = new Sections(originSections);
        Section newSection = new Section(newUpStation, newDownStation,  3);

        assertThrows(IllegalArgumentException.class, () -> sections.validateInsert(newSection));
    }

    @DisplayName("새로운 구간이 가운데에 삽입될 때 업데이트할 기존 구간을 반환한다.")
    @Test
    void oldSection() {
        Station upStation = new Station(4L, "잠실나루역");
        Station midStation = new Station(2L, "잠실역");
        Station downStation = new Station(1L, "강변역");

        List<Section> originSections = new ArrayList<>();
        Section oldSection = new Section(upStation, downStation, 10);
        originSections.add(oldSection);
        Sections sections = new Sections(originSections);
        Section newSection = new Section(upStation, midStation,  3);
        Section newSection2 = new Section(midStation, downStation,  3);

        assertEquals(sections.oldSection(newSection), oldSection);
        assertEquals(sections.oldSection(newSection2), oldSection);
    }

    @DisplayName("새로운 구간이 삽입될 수 있도록 기존 구간을 잘라서 반환한다.")
    @Test
    void cutOldSection() {
        Station upStation = new Station(4L, "잠실나루역");
        Station midStation = new Station(2L, "잠실역");
        Station downStation = new Station(1L, "강변역");

        List<Section> originSections = new ArrayList<>();
        Section oldSection = new Section(upStation, downStation, 10);
        originSections.add(oldSection);
        Sections sections = new Sections(originSections);

        Section upMidSection = new Section(upStation, midStation,  3);
        Section midDownSection = new Section(midStation, downStation, 7);

        assertEquals(sections.cut(oldSection, upMidSection), midDownSection);
        assertEquals(sections.cut(oldSection, midDownSection), upMidSection);
    }

    @DisplayName("새로운 구간을 삽입할 때 가운데에 삽입되는지 확인한다.")
    @Test
    void isInsertedMiddle() {
        Station upStation = new Station(4L, "잠실나루역");
        Station midStation = new Station(2L, "잠실역");
        Station downStation = new Station(1L, "강변역");

        List<Section> originSections = new ArrayList<>();
        originSections.add(new Section(upStation, downStation,  10));
        Sections sections = new Sections(originSections);
        Section newSection = new Section(upStation, midStation,  3);

        assertTrue(sections.isInsertedMiddle(newSection));
    }

    @DisplayName("새로운 구간을 삽입할 때 끝 구간에 삽입되는지 확인한다.")
    @Test
    void isInsertedEnd() {
        Station upStation = new Station(4L, "잠실나루역");
        Station downStation = new Station(2L, "잠실역");
        Station newDownStation = new Station(1L, "강변역");

        List<Section> originSections = new ArrayList<>();
        originSections.add(new Section(upStation, downStation,  10));
        Sections sections = new Sections(originSections);
        Section newSection = new Section(downStation, newDownStation,  3);

        assertFalse(sections.isInsertedMiddle(newSection));
    }

    @DisplayName("새로운 구간을 중간에 삽입할 때의 거리가 기존 구간의 거리와 같거나 길면 예외를 던진다.")
    @Test
    void greaterOrEqualDistance() {
        Station upStation = new Station(4L, "잠실나루역");
        Station midStation = new Station(2L, "잠실역");
        Station downStation = new Station(1L, "강변역");

        List<Section> originSections = new ArrayList<>();
        Section oldSection = new Section(upStation, downStation, 10);
        originSections.add(oldSection);
        Sections sections = new Sections(originSections);
        Section newSection = new Section(upStation, midStation,  10);

        assertThrows(IllegalArgumentException.class, () -> sections.cut(oldSection, newSection));
    }

    @DisplayName("마지막 구간을 제거한다.")
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

        // when & then
        assertDoesNotThrow(() -> sections.validateDelete(newDownStation));
    }

    @DisplayName("마지막이 아닌 구간을 제거하면 예외를 던진다.")
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
        assertThrows(IllegalArgumentException.class, () -> sections.validateDelete(downStation));
    }

    @DisplayName("구간이 1개만 있을 때 구간을 제거하면 예외를 던진다.")
    @Test
    void deleteSectionOneAndOnly() {
        // given
        Station upStation = new Station(4L, "강변역");
        Station downStation = new Station(3L, "구의역");
        int distance = 10;

        Sections sections = new Sections(List.of(new Section(upStation, downStation, distance)));

        // when & then
        assertThrows(IllegalStateException.class, () -> sections.validateDelete(downStation));
    }
}
