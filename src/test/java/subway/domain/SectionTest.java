package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalSectionException;
import subway.fixture.LineFixture;
import subway.fixture.SectionFixture;
import subway.fixture.StationFixture;

import static org.assertj.core.api.Assertions.*;

class SectionTest {

    @DisplayName("section 생성에 성공한다.")
    @Test
    void createSectionTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Station 첫번째역 = StationFixture.첫번째역();
        final Station 두번째역 = StationFixture.두번째역();

        // when & then
        assertThatNoException().isThrownBy(() -> new Section(신분당선, 첫번째역, 두번째역, SectionFixture.DEFAULT_DISTANCE));
    }

    @DisplayName("0 이하인 길이로 section 생성에 실패한다.")
    @Test
    void validateDistanceTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Station 첫번째역 = StationFixture.첫번째역();
        final Station 두번째역 = StationFixture.두번째역();

        // when & then
        assertThatThrownBy(() -> new Section(신분당선, 첫번째역, 두번째역, 0))
                .hasMessage("구간 길이는 0보다 커야합니다.")
                .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("상행 및 하행역이 같아 section 생성에 실패한다.")
    @Test
    void validateStationsTest() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final Station 첫번째역 = StationFixture.첫번째역();

        // when & then
        assertThatThrownBy(() -> new Section(신분당선, 첫번째역, 첫번째역, SectionFixture.DEFAULT_DISTANCE))
                .hasMessage("상행역과 하행역은 같을 수 없습니다.")
                .isInstanceOf(IllegalSectionException.class);
    }
}
