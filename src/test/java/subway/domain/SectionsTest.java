package subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayException;
import subway.fixture.LineFixture;
import subway.fixture.SectionFixture;
import subway.fixture.StationFixture;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SectionsTest {

    @DisplayName("Sections 생성에 성공한다.")
    @Test
    void createSectionsTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Section 첫번째역_두번째역_구간 = SectionFixture.첫번째역_두번째역_구간(신분당선);

        // when & then
        assertThatNoException()
                .isThrownBy(() -> new Sections(List.of(첫번째역_두번째역_구간)));
    }

    @DisplayName("해당 구간이 추가 가능한 구간인지 검증에 성공한다.")
    @Test
    void validateSectionTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Sections 신분당선_구간들 = create_신분당선_구간들();

        // when & then
        assertThatNoException()
                .isThrownBy(() -> 신분당선_구간들.findConnectedSection(SectionFixture.다섯번째역_여섯번째역_구간(신분당선)));
    }

    @DisplayName("두 역이 모두 노선에 존재하는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailBothContainTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Sections 신분당선_구간들 = create_신분당선_구간들();

        final Section 첫번째역_두번째역_구간 = new Section(
                신분당선, StationFixture.첫번째역(), StationFixture.두번째역(), SectionFixture.DEFAULT_DISTANCE - 1
        );

        // when & then
        assertThatThrownBy(() -> 신분당선_구간들.findConnectedSection(첫번째역_두번째역_구간))
                .isInstanceOf(SubwayException.class)
                .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("두 역이 모두 노선에 존재하지 않는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailNeitherContainTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Sections 신분당선_구간들 = create_신분당선_구간들();

        // when & then
        assertThatThrownBy(() -> 신분당선_구간들.findConnectedSection(SectionFixture.여섯번째역_일곱번째역_구간(신분당선)))
                .isInstanceOf(SubwayException.class)
                .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void validateSectionDistanceFailTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Sections 신분당선_구간들 = create_신분당선_구간들();

        final Section 첫번째역_여섯번째역_구간 = new Section(
                신분당선, StationFixture.첫번째역(), StationFixture.여섯번째역(), SectionFixture.DEFAULT_DISTANCE
        );

        // when & then
        assertThatThrownBy(() -> 신분당선_구간들.findConnectedSection(첫번째역_여섯번째역_구간))
                .isInstanceOf(SubwayException.class)
                .hasMessage("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 검증에 성공한다.")
    @Test
    void validateSectionDistanceTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Sections 신분당선_구간들 = create_신분당선_구간들();

        final Section 첫번째역_여섯번째역_구간 = new Section(
                신분당선, StationFixture.첫번째역(), StationFixture.여섯번째역(), SectionFixture.DEFAULT_DISTANCE - 1
        );

        // when & then
        assertThatNoException()
                .isThrownBy(() -> 신분당선_구간들.findConnectedSection(첫번째역_여섯번째역_구간));
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
                .isInstanceOf(SubwayException.class);
    }

    private Sections create_신분당선_구간들() {
        final Line 신분당선 = LineFixture.신분당선();
        return new Sections(List.of(
                SectionFixture.첫번째역_두번째역_구간(신분당선),
                SectionFixture.두번째역_세번째역_구간(신분당선),
                SectionFixture.세번째역_네번째역_구간(신분당선),
                SectionFixture.네번째역_다섯번째역_구간(신분당선)
        ));
    }
}
