package subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static subway.fixture.SectionFixture.*;
import static subway.fixture.StationFixture.*;

class PathManagerTest {

    @DisplayName("PathManager를 생성하는 데 성공한다.")
    @Test
    void createPathManager() {
        // given
        final List<Station> stations = List.of();
        final List<Section> sections = List.of();

        // when & then
        assertThatNoException().isThrownBy(() -> PathManager.create(stations, sections));
    }

    private PathManager pathManager() {
        /*
          <지하철 노선도>
          범계 -10- 경마공원 -10- 사당 -10- 신용산
                     |         |
                     50       10
                     ㄴ ------ 강남 -10- 잠실
          여의도 -10- 노량진
         */

        final List<Station> stations = List.of(
                범계역(), 경마공원역(), 사당역(), 신용산역(), 강남역(), 잠실역(), 여의도역(), 노량진역()
        );

        final List<Section> sections = List.of(
                범계역_경마공원역_구간(DEFAULT_DISTANCE),
                경마공원역_사당역_구간(DEFAULT_DISTANCE),
                사당역_신용산역_구간(DEFAULT_DISTANCE),
                경마공원역_강남역_구간(DEFAULT_DISTANCE * 5),
                사당역_강남역_구간(DEFAULT_DISTANCE),
                강남역_잠실역_구간(DEFAULT_DISTANCE),
                여의도역_노량진역_구간(DEFAULT_DISTANCE)
        );

        return PathManager.create(stations, sections);
    }
}
