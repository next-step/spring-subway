package subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalSectionException;
import subway.fixture.LineFixture;
import subway.fixture.SectionFixture;
import subway.fixture.StationFixture;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SectionsTest {

    @DisplayName("Sections 생성에 성공한다.")
    @Test
    void createSectionsTest() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");

        // when & then
        assertThatNoException()
                .isThrownBy(() -> new Sections(List.of(new Section(line, station1, station2, 10))));
    }

    @DisplayName("해당 구간이 추가 가능한 구간인지 검증에 성공한다.")
    @Test
    void validateSectionTest() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station5 = new Station(5L, "상록수");
        final Station station6 = new Station(6L, "산본");

        final Sections sections = new Sections(createSections());

        // when & then
        assertThatNoException()
                .isThrownBy(() -> sections.findConnectedSection(new Section(line, station5, station6, 10)));
    }

    @DisplayName("두 역이 모두 노선에 존재하는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailBothContainTest() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station4 = new Station(4L, "중앙");

        final Sections sections = new Sections(createSections());

        // when & then
        assertThatThrownBy(() -> sections.findConnectedSection(new Section(line, station1, station4, 5)))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("두 역이 모두 노선에 존재하지 않는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailNeitherContainTest() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station6 = new Station(6L, "산본");
        final Station station7 = new Station(7L, "범계");

        final Sections sections = new Sections(createSections());

        // when & then
        assertThatThrownBy(() -> sections.findConnectedSection(new Section(line, station6, station7, 5)))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void validateSectionDistanceFailTest() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station6 = new Station(6L, "산본");

        final Sections sections = new Sections(createSections());

        // when & then
        assertThatThrownBy(() -> sections.findConnectedSection(new Section(line, station1, station6, 10)))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 검증에 성공한다.")
    @Test
    void validateSectionDistanceTest() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station6 = new Station(6L, "산본");

        final Sections sections = new Sections(createSections());

        // when & then
        assertThatNoException()
                .isThrownBy(() -> sections.findConnectedSection(new Section(line, station1, station6, 3)));
    }

    @DisplayName("상행 종점 삭제 시 section 리스트를 반환하는 데 성공한다.")
    @Test
    void disconnectStartStationTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Sections 신분당선_구간들 = new Sections(List.of(
                SectionFixture.첫번째역_두번째역_구간(신분당선),
                SectionFixture.두번째역_세번째역_구간(신분당선),
                SectionFixture.세번째역_네번째역_구간(신분당선),
                SectionFixture.네번째역_다섯번째역_구간(신분당선)
        ));

        // when
        final List<Section> disconnectedSections = 신분당선_구간들.disconnect(StationFixture.첫번째역().getId());

        // then
        assertThat(disconnectedSections)
                .hasSize(1)
                .containsExactly(SectionFixture.첫번째역_두번째역_구간(신분당선));
    }

    @DisplayName("하행 종점 삭제 시 section 리스트를 반환하는 데 성공한다.")
    @Test
    void disconnectEndStationTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Sections 신분당선_구간들 = new Sections(List.of(
                SectionFixture.첫번째역_두번째역_구간(신분당선),
                SectionFixture.두번째역_세번째역_구간(신분당선),
                SectionFixture.세번째역_네번째역_구간(신분당선),
                SectionFixture.네번째역_다섯번째역_구간(신분당선)
        ));

        // when
        final List<Section> disconnectedSections = 신분당선_구간들.disconnect(StationFixture.다섯번째역().getId());

        // then
        assertThat(disconnectedSections)
                .hasSize(1)
                .containsExactly(SectionFixture.네번째역_다섯번째역_구간(신분당선));
    }

    @DisplayName("종점이 아닌 역 삭제 시 section 리스트를 반환하는 데 성공한다.")
    @Test
    void disconnectInnerStationTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Sections 신분당선_구간들 = new Sections(List.of(
                SectionFixture.첫번째역_두번째역_구간(신분당선),
                SectionFixture.두번째역_세번째역_구간(신분당선),
                SectionFixture.세번째역_네번째역_구간(신분당선),
                SectionFixture.네번째역_다섯번째역_구간(신분당선)
        ));

        // when
        final List<Section> disconnectedSections = 신분당선_구간들.disconnect(StationFixture.세번째역().getId());

        // then
        assertThat(disconnectedSections)
                .hasSize(2)
                .containsExactly(SectionFixture.두번째역_세번째역_구간(신분당선), SectionFixture.세번째역_네번째역_구간(신분당선));
    }

    @DisplayName("구간이 두 개 미만일 시 삭제에 실패한다.")
    @Test
    void disconnectWithInvalidSectionsTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Sections 신분당선_구간들 = new Sections(List.of(SectionFixture.첫번째역_두번째역_구간(신분당선)));

        // when & then
        assertThatThrownBy(() -> 신분당선_구간들.disconnect(StationFixture.첫번째역().getId()))
                .hasMessage("노선에 구간이 최소 2개가 있어야 삭제가 가능합니다.")
                .isInstanceOf(IllegalSectionException.class);
    }

    private List<Section> createSections() {
        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");
        final Station station3 = new Station(3L, "한대앞");
        final Station station4 = new Station(4L, "중앙");
        final Station station5 = new Station(5L, "상록수");

        final List<Section> sections = new ArrayList<>();
        sections.add(new Section(1L, line, station4, station3, 10));
        sections.add(new Section(2L, line, station3, station1, 10));
        sections.add(new Section(3L, line, station1, station2, 10));
        sections.add(new Section(4L, line, station2, station5, 10));
        return sections;
    }
}
