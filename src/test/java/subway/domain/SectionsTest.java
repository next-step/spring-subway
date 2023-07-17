package subway.domain;

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

    @DisplayName("비어있는 구간으로 생성하는 테스트")
    @Test
    void createByEmptySection() {
        assertThrows(IllegalArgumentException.class, () -> new Sections(Collections.emptyList()));
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
}
