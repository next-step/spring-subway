package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PathFinder 테스트")
public class PathFinderTest {

    private Long 출발역_아이디;
    private Long 도착역_아이디;
    private Station 교대역 = new Station(5L, "교대역");
    private Station 강남역 = new Station(6L, "강남역");
    private Station 남부터미널역 = new Station(7L, "남부터미널역");
    private Station 양재역 = new Station(8L, "양재역");
    private Line 이호선 = new Line(2L, "2호선", "빨강");
    private Line 삼호선 = new Line(3L, "삼호선", "노랑");
    private Line 신분당선 = new Line(4L, "신분당선", "파랑");
    private Section 교대_강남 = new Section(2L, 교대역, 강남역, 이호선, 1);
    private Section 교대_남부터미널 = new Section(3L, 교대역, 남부터미널역, 삼호선, 2);
    private Section 강남_양재 = new Section(4L, 강남역, 양재역, 신분당선, 5);
    private Section 남부터미널_양재 = new Section(5L, 남부터미널역, 양재역, 삼호선, 3);
    private List<Section> 교대_강남_남부터미널_양재;

    @BeforeEach
    void setUp() {
        교대_강남_남부터미널_양재 = List.of(교대_강남, 교대_남부터미널, 강남_양재, 남부터미널_양재);
    }

    @Test
    @DisplayName("성공 : 객체 생성")
    void create() {
        assertThatNoException()
            .isThrownBy(() -> new PathFinder(교대_강남_남부터미널_양재, 교대역, 양재역));
    }

    @Test
    @DisplayName("성공1 : 출발역과 도착역의 최단 거리 역 정보를 리턴")
    void successFindStations() {
        // when
        PathFinder pathFinder = new PathFinder(교대_강남_남부터미널_양재, 교대역, 양재역);

        // then
        assertThat(pathFinder.findShortestStations()).containsExactly(교대역, 남부터미널역, 양재역);
    }

    @Test
    @DisplayName("성공2 : 출발역과 도착역의 최단 거리 역 정보를 리턴")
    void successFindStations2() {
        // when
        PathFinder pathFinder = new PathFinder(교대_강남_남부터미널_양재, 교대역, 남부터미널역);

        // then
        assertThat(pathFinder.findShortestStations()).containsExactly(교대역, 남부터미널역);
    }

    @Test
    @DisplayName("성공1 : 출발역과 도착역의 최단 거리 리턴")
    void successFindShortestDistance1() {
        // when
        PathFinder pathFinder = new PathFinder(교대_강남_남부터미널_양재, 교대역, 양재역);

        // then
        assertThat(pathFinder.findShortestDistance()).isEqualTo(5D);
    }

    @Test
    @DisplayName("성공2 : 출발역과 도착역의 최단 거리 리턴")
    void successFindShortestDistance2() {
        // when
        PathFinder pathFinder = new PathFinder(교대_강남_남부터미널_양재, 교대역, 남부터미널역);

        // then
        assertThat(pathFinder.findShortestDistance()).isEqualTo(2D);
    }

}
