package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("경로 조회 도메인 테스트")
class PathFinderTest {

    @DisplayName("구간 리스트와 출발역, 도착역을 인자로 받아 PathFinder를 생성한다.")
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
        assertDoesNotThrow(() -> new PathFinder(sections));
    }

    @DisplayName("구간 리스트를 인자로 받아 최단 경로를 이루는 역을 반환한다.")
    @Test
    void findShortestPath() {
        // given
        /**
         * 교대역    --- 1km ---    강남역
         * |                        |
         * 100km                    1km
         * |                        |
         * 남부터미널역 --- 100km ---  양재역
         */
        final Station 교대역 = new Station(1L, "교대역");
        final Station 강남역 = new Station(2L, "강남역");
        final Station 양재역 = new Station(3L, "양재역");
        final Station 남부터미널역 = new Station(4L, "남부터미널역");

        final Section 교대_강남 = new Section(교대역, 강남역, 1);
        final Section 강남_양재 = new Section(강남역, 양재역, 1);
        final Section 교대_남부터미널 = new Section(교대역, 남부터미널역, 100);
        final Section 남부터미널_양재 = new Section(남부터미널역, 양재역, 100);

        final List<Section> sections = new ArrayList<>(List.of(교대_강남, 강남_양재, 교대_남부터미널, 남부터미널_양재));

        final PathFinder pathFinder = new PathFinder(sections);
        final PathFinderResult result = pathFinder.findShortestPath(교대역, 양재역);

        // when & then
        assertEquals(2, result.getDistance());
        assertEquals(3, result.getPaths().size());
        assertThat(result.getPaths()).containsExactlyInAnyOrder(1L, 2L, 3L);
    }
}
