package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IncorrectRequestException;

import java.util.ArrayList;
import java.util.List;

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

        assertThrows(IncorrectRequestException.class, () -> sections.validateInsert(newSection));
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

        assertThrows(IncorrectRequestException.class, () -> sections.validateInsert(newSection));
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

        assertEquals(sections.findOverlappedSection(newSection), oldSection);
        assertEquals(sections.findOverlappedSection(newSection2), oldSection);
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

    @DisplayName("구간 내에서 역을 제거할 수 있는지 확인한다.")
    @Test
    void deleteLastSection() {
        // given
        Station upStation = new Station(4L, "강변역");
        Station midStation = new Station(3L, "구의역");
        Station downStation = new Station(2L, "건대입구역");
        int distance = 10;

        Sections sections = new Sections(List.of(
                new Section(upStation, midStation, distance),
                new Section(midStation, downStation, distance)));

        // when & then
        assertDoesNotThrow(() -> sections.validateDelete(upStation));
        assertDoesNotThrow(() -> sections.validateDelete(midStation));
        assertDoesNotThrow(() -> sections.validateDelete(downStation));
    }

    @DisplayName("존재하지 않는 역을 제거하면 예외를 던진다.")
    @Test
    void deleteNotExist() {
        // given
        Station upStation = new Station(4L, "강변역");
        Station midStation = new Station(3L, "구의역");
        Station downStation = new Station(1L, "잠실역");
        Station notExist = new Station(2L, "건대입구역");
        int distance = 10;

        Sections sections = new Sections(List.of(
                new Section(upStation, midStation, distance),
                new Section(midStation, downStation, distance)));

        // when & then
        assertThrows(IncorrectRequestException.class, () -> sections.validateDelete(notExist));
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
        assertThrows(IncorrectRequestException.class, () -> sections.validateDelete(downStation));
    }
}
