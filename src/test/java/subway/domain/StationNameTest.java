package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import subway.exception.SubwayIllegalArgumentException;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StationNameTest {

    @Test
    @DisplayName("역 이름을 정상적으로 생성한다.")
    void create() {
        /* given */
        final String value = "잠실";

        /* when & then */
        assertDoesNotThrow(() -> new StationName(value));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("값이 존재하지 않거나 공백일 경우 SubwayIllegalArgumentException을 던진다.")
    void createFailWithNullOrEmpty(final String value) {
        /* given */


        /* when & then */
        assertThatThrownBy(() -> new StationName(value))
                .isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("역 이름은 반드시 입력해야합니다. 입력 값: " + value);
    }

    @Test
    @DisplayName("값이 같으면 동일하다.")
    void equalsAndHashValue() {
        /* given */
        final StationName name1 = new StationName("잠실");
        final StationName name2 = new StationName("잠실");

        /* when */
        final Set<StationName> names = new HashSet<>();
        names.add(name1);
        names.add(name2);

        /* then */
        assertThat(name1).isEqualTo(name2);
        assertThat(names).hasSize(1);
    }
}
