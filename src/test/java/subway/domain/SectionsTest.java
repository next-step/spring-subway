package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {

    @DisplayName("새로운 구간을 등록하는 테스트")
    @Test
    void insertNewSection() {
        Sections sections = new Sections(List.of(new Section()));
        assertDoesNotThrow(() -> sections.insert(new Section()));
    }

    @DisplayName("비어있는 구간으로 생성하는 테스트")
    @Test
    void createByEmptySection() {
        assertThrows(IllegalArgumentException.class, () -> new Sections(Collections.emptyList()));
    }

    @DisplayName("새로운 구간의 상행역이 하행 종점역과 다를 경우 예외로 처리")
    @Test
    void differentStation() {
        long upStationId = 4;
        long downStationId = 2;
        long newUpStationId = 8;
        long newDownStationId = 9;
        long lineId = 1;
        int distance = 10;

        Sections sections = new Sections(List.of(new Section(upStationId, downStationId, lineId, distance)));
        Section  newSection = new Section(newUpStationId, newDownStationId, lineId, distance);

        assertThrows(IllegalArgumentException.class, () -> sections.insert(newSection));
    }
}
