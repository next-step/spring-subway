package subway.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static subway.exception.ErrorCode.NOT_CONNECTED_BETWEEN_START_AND_END_PATH;
import static subway.exception.ErrorCode.NOT_FOUND_END_PATH_POINT;
import static subway.exception.ErrorCode.NOT_FOUND_START_PATH_POINT;
import static subway.exception.ErrorCode.SAME_START_END_PATH_POINT;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayException;

public class PathGraphTest {

    @Test
    @DisplayName("Graph 객체를 정상적으로 생성한다.")
    void graph_객체_정상_생성() {
        // given
        Station 서울역 = new Station("서울역");
        Station 오이도역 = new Station("오이도역");
        Section section = new Section(서울역, 오이도역, new Distance(10));
        List<Section> sections = List.of(section);

        // when &then
        assertThatCode(() -> new PathGraph(new Sections(sections)))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이름이 같지 않고, 연결되어 있다면, Path를 생성할 수 있다.")
    void 길_생성_정상_동작() {
        // given
        Station 서울대입구역 = new Station("서울대입구역");
        Station 상도역 = new Station("상도역");

        // when
        PathGraph graph = new PathGraph(
            List.of(new Section(서울대입구역, 상도역, new Distance(10))
            )
        );
        assertThatCode(() -> graph.createPath(서울대입구역, 상도역))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("출발역과, 도착역의 이름이 같다면 , Path를 생성할 수 없다")
    void 이름이_같다면_Path_생성_오류_동작() {
        // given
        Station 서울대입구역 = new Station("서울대입구역");
        Station 신대방역 = new Station("신대방역");

        Section section = new Section(서울대입구역, 신대방역, new Distance(10));
        PathGraph graph = new PathGraph(List.of(section));

        Station 서울대입구역2 = new Station("서울대입구역");

        // when & then
        assertThatCode(() -> graph.createPath(서울대입구역, 서울대입구역2))
            .isInstanceOf(SubwayException.class)
            .hasMessage(SAME_START_END_PATH_POINT.getMessage());
    }

    @Test
    @DisplayName("출발점과 도착역이 연결이 되어있지 않다면 Path를 생성할 수 없습니다")
    void 출발역_도착역_연결_안되면_Path_생성_불가능() {
        // given
        Station 서울대입구역 = new Station("서울대입구역");
        Station 신대방역 = new Station("신대방역");
        Station 오이도역 = new Station("오이도역");
        Station 신당역 = new Station("신당역");

        Section section = new Section(서울대입구역, 신대방역, new Distance(10));
        Section section2 = new Section(오이도역, 신당역, new Distance(10));
        // when & then
        PathGraph graph = new PathGraph(List.of(section, section2));
        assertThatCode(() -> graph.createPath(서울대입구역, 오이도역))
            .isInstanceOf(SubwayException.class)
            .hasMessage(NOT_CONNECTED_BETWEEN_START_AND_END_PATH.getMessage());
    }

    @Test
    @DisplayName("존재하지 않은 출발역을 조회할 경우 에러")
    void 존재하지_않는_출발역_조회_할_경우_에러() {

        // given
        Station 서울대입구역 = new Station("서울대입구역");
        Station 신대방역 = new Station("신대방역");
        Station 대림역 = new Station("대림역");

        Section section = new Section(서울대입구역, 신대방역, new Distance(10));
        PathGraph graph = new PathGraph(List.of(section));
        // when & then
        assertThatCode(() -> graph.createPath(대림역, 신대방역))
            .isInstanceOf(SubwayException.class)
            .hasMessage(NOT_FOUND_START_PATH_POINT.getMessage());
    }

    @Test
    @DisplayName("존재하지 않은 도착역을 조회할 경우")
    void 존재하지_않는_도착역_조회_할_경우_에러() {

        // given
        Station 서울대입구역 = new Station("서울대입구역");
        Station 신대방역 = new Station("신대방역");
        Station 대림역 = new Station("대림역");

        Section section = new Section(서울대입구역, 신대방역, new Distance(10));
        PathGraph graph = new PathGraph(List.of(section));
        // when & then
        assertThatCode(() -> graph.createPath(서울대입구역, 대림역))
            .isInstanceOf(SubwayException.class)
            .hasMessage(NOT_FOUND_END_PATH_POINT.getMessage());
    }
}
