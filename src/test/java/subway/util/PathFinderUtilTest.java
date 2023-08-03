package subway.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import subway.common.util.PathFinder;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.PathFinderResult;
import subway.exception.FindPathException;
import subway.exception.SectionException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PathFinder.class})
@DisplayName("경로 조회 유틸 테스트")
class PathFinderUtilTest {

    @Autowired
    private PathFinder pathFinder;

    Station 교대역;
    Station 강남역;
    Station 양재역;
    Station 남부터미널역;
    Section 교대_강남;
    Section 강남_양재;
    Section 교대_남부터미널;
    Section 남부터미널_양재;
    List<Section> sections;

    @BeforeEach
    void setUp() {
        /**
         * 교대역    --- 1km ---    강남역
         * |                        |
         * 100km                    1km
         * |                        |
         * 남부터미널역 --- 100km ---  양재역
         */
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");

        교대_강남 = new Section(교대역, 강남역, 1);
        강남_양재 = new Section(강남역, 양재역, 1);
        교대_남부터미널 = new Section(교대역, 남부터미널역, 100);
        남부터미널_양재 = new Section(남부터미널역, 양재역, 100);

        sections = new ArrayList<>(List.of(교대_강남, 강남_양재, 교대_남부터미널, 남부터미널_양재));
    }


    @DisplayName("구간 리스트가 비어 있을 경우 예외를 던진다.")
    @Test
    void createPathFinderByEmptySections() {
        assertThrows(SectionException.class,
                () -> pathFinder.findShortestPath(Collections.emptyList(), 교대역, 강남역));
    }

    @DisplayName("(정방향) 구간 리스트를 인자로 받아 최단 경로를 이루는 역을 반환한다.")
    @Test
    void findShortestPathInOrder() {
        // when
        final PathFinderResult result = pathFinder.findShortestPath(sections, 교대역, 양재역);

        // then
        assertEquals(2, result.getDistance().getValue());
        assertEquals(3, result.getPaths().size());
        assertThat(result.getPaths()).containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @DisplayName("(역방향) 구간 리스트를 인자로 받아 최단 경로를 이루는 역을 반환한다.")
    @Test
    void findShortestPathInReversedOrder() {
        // when
        final PathFinderResult result = pathFinder.findShortestPath(sections, 양재역, 교대역);

        // then
        assertEquals(2, result.getDistance().getValue());
        assertEquals(3, result.getPaths().size());
        assertThat(result.getPaths()).containsExactlyInAnyOrder(3L, 2L, 1L);
    }

    @DisplayName("최단 경로가 없을 경우 예외를 던진다.")
    @Test
    void notExistsShortestPath() {
        // given
        /**
         * 교대역    --- 1km ---    강남역
         * |                        |
         * 100km                    1km
         * |                        |
         * 남부터미널역 --- 100km ---  양재역            면목역 --- 1km --- 상봉역
         */
        final Station 면목역 = new Station(5L, "면목역");
        final Station 상봉역 = new Station(6L, "상봉역");

        final Section 면목_상봉 = new Section(면목역, 상봉역, 1);

        final List<Section> sections = new ArrayList<>(List.of(교대_강남, 강남_양재, 교대_남부터미널, 남부터미널_양재, 면목_상봉));

        // when & then
        assertThrows(FindPathException.class, () -> pathFinder.findShortestPath(sections, 교대역, 면목역));
    }
}
