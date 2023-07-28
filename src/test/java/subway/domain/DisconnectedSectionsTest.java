package subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalStationException;
import subway.fixture.LineFixture;
import subway.fixture.SectionFixture;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DisconnectedSectionsTest {

    @DisplayName("업데이트 할 section이 존재할 때, DisconnectedSections 생성에 성공한다.")
    @Test
    void createDisconnectedSectionsHasUpdateSection() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final List<Section> sections = List.of(
                SectionFixture.첫번째역_두번째역_구간(신분당선),
                SectionFixture.두번째역_세번째역_구간(신분당선)
        );

        // when & then
        assertThatNoException().isThrownBy(() -> DisconnectedSections.of(sections));
    }

    @DisplayName("업데이트 할 section이 존재하지 않을 때, DisconnectedSections 생성에 성공한다.")
    @Test
    void createDisconnectedSectionsNotHasUpdateSection() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final List<Section> sections = List.of(
                SectionFixture.첫번째역_두번째역_구간(신분당선)
        );

        // when & then
        assertThatNoException().isThrownBy(() -> DisconnectedSections.of(sections));
    }

    @DisplayName("삭제할 역이 존재하지 않아 DisconnectedSections 생성에 실패한다.")
    @Test
    void createDisconnectedSectionsWithEmptySections() {
        // given
        final List<Section> sections = List.of();

        // when & then
        assertThatThrownBy(() -> DisconnectedSections.of(sections))
                .hasMessage("해당 노선에 삭제할 역이 존재하지 않습니다.")
                .isInstanceOf(IllegalStationException.class);
    }

    @DisplayName("이상한 sections로 DisconnectedSections 생성에 실패한다.")
    @Test
    void createDisconnectedSectionsWithTooManySections() {
        // given
        final Line 신분당선 = LineFixture.신분당선();
        final List<Section> sections = List.of(
                SectionFixture.첫번째역_두번째역_구간(신분당선),
                SectionFixture.두번째역_세번째역_구간(신분당선),
                SectionFixture.세번째역_네번째역_구간(신분당선)
        );
        // when & then
        assertThatThrownBy(() -> DisconnectedSections.of(sections))
                .hasMessage("[ERROR] 노선의 구간들이 올바르게 연결되어 있지 않습니다.")
                .isInstanceOf(IllegalArgumentException.class);
    }
}
