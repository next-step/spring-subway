package subway.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SectionDuplicationStationIdException;
import subway.exception.SectionNotConnectingStationException;
import subway.exception.SectionRemoveLastStationException;

class SectionsTest {

    @DisplayName("빈 노선일 때 구간을 등록하면 노선도에 구간이 추가된다.")
    @Test
    void addSection() {
        // given
        Sections 대전_1호선_구간들 = new Sections(new ArrayList<>());
        Section addSection = new Section(1L, 대전_노선도_1호선().getId(), 반석역().getId(), 지족역().getId(), 10);

        // when

        // then
        assertDoesNotThrow(() -> 대전_1호선_구간들.addSection(addSection));
        assertEquals(1, 대전_1호선_구간들.getSections().size());
    }

    @DisplayName("빈 노선일 때 구간을 등록하면 노선도에 구간이 추가된다.")
    @Test
    void addSection2() {
        // given
        Sections 대전_1호선_구간들 = new Sections(new ArrayList<>(List.of(반석역_지족역_구간())));
        Section addSection = new Section(1L, 대전_노선도_1호선().getId(), 지족역().getId(), 노은역().getId(), 5);

        // when

        // then
        assertDoesNotThrow(() -> 대전_1호선_구간들.addSection(addSection));
        assertEquals(2, 대전_1호선_구간들.getSections().size());
    }

    @DisplayName("이미 등록된 구간에 등록된 역이 있을 때 구간을 추가하면 에러를 반환한다.")
    @Test
    void addSectionFalse() {
        // given
        Sections 대전_1호선_구간들 = new Sections(new ArrayList<>(List.of(반석역_지족역_구간())));
        Section addSection = new Section(1L, 대전_노선도_1호선().getId(), 지족역().getId(), 반석역().getId(), 5);

        // when

        // then
        assertThrows(SectionDuplicationStationIdException.class, () -> 대전_1호선_구간들.addSection(addSection));
    }

    @DisplayName("상행역은 해당 노선에 등록되어있는 하행 종점역이 아닐 경우 구간을 추가하면 에러를 반환한다.")
    @Test
    void addSectionFalse2() {
        // given
        Sections 대전_1호선_구간들 = new Sections(new ArrayList<>(List.of(반석역_지족역_구간())));
        Section addSection = new Section(1L, 대전_노선도_1호선().getId(), 노은역().getId(), 월드컵경기장역().getId(), 5);

        // when

        // then
        assertThrows(SectionNotConnectingStationException.class, () -> 대전_1호선_구간들.addSection(addSection));
    }


    @DisplayName("지하철 노선에 등록된 역이 있을 때 하행 종점역 아이디를 입력하면 종점역이 제거된다.")
    @Test
    void removeLastSection() {
        // given
        Sections 대전_1호선_구간들 = new Sections(new ArrayList<>(List.of(반석역_지족역_구간())));

        // when

        // then
        assertDoesNotThrow(() -> 대전_1호선_구간들.removeLastSection(지족역().getId()));
        assertEquals(0, 대전_1호선_구간들.getSections().size());
    }

    @DisplayName("지하철 노선을 제거할 때 하행 종점역이 아닌 역 아이디를 입력하면 에러를 반환한다.")
    @Test
    void removeLastSectionFalse() {
        // given
        Sections 대전_1호선_구간들 = new Sections(new ArrayList<>(List.of(반석역_지족역_구간())));

        // when

        // then
        assertThrows(SectionRemoveLastStationException.class, () -> 대전_1호선_구간들.removeLastSection(반석역().getId()));
    }

    private Line 대전_노선도_1호선() {
        return new Line(1L, "1호선", "Gray");
    }

    private Station 반석역() {
        return new Station(1L, "반석");
    }

    private Station 지족역() {
        return new Station(2L, "지족");
    }

    private Station 노은역() {
        return new Station(3L, "노은");
    }

    private Station 월드컵경기장역() {
        return new Station(4L, "월드컵경기장");
    }

    private Section 반석역_지족역_구간() {
        return new Section(1L, 대전_노선도_1호선().getId(), 반석역().getId(), 지족역().getId(), 10);
    }
}
