package subway.domain;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("경로 조회 도메인 테스트")
class PathFinderTest {

    @DisplayName("구간 리스트를 인자로 받아 PathFinder를 생성한다.")
    @Test
    void createPathFinder() {
        // given
        final Station 교대역 = new Station(1L, "교대역");
        final Station 강남역 = new Station(2L, "강남역");
        final Station 역삼역 = new Station(3L, "역삼역");
        final Section 교대_강남 = new Section(교대역, 강남역, 2);
        final Section 강남_역삼 = new Section(강남역, 역삼역, 3);
        final List<Section> sections = new ArrayList<>(List.of(교대_강남, 강남_역삼));

        // when & then
        Assertions.assertDoesNotThrow(() -> new PathFinder(sections));
    }
}
