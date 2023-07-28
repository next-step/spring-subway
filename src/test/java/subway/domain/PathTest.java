package subway.domain;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathTest {

    @Test
    @DisplayName("이름이 같지 않고, 연결되어 있다면 Path를 생성할 수 있다.")
    void 길_생성_정상_동작() {
        // given
        Station 서울대입구역 = new Station("서울대입구역");
        Station 상도역 = new Station("상도역");

        // when
        Path path = new Path(
            List.of(new Section(서울대입구역, 상도역, new Distance(10))
            )
        );
        assertThatCode(() -> path.createPath(서울대입구역, 상도역))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이름이 같다면 Path를 생성할 수 없다")
    void 이름이_같다면_Path_생성_오류_동작() {
        // given
        Station 서울대입구역 = new Station("서울대입구역");
        Station 신대방역 = new Station("신대방역");

        Section section = new Section(서울대입구역, 신대방역, new Distance(10));
        Path path = new Path(List.of(section));

        Station 서울대입구역2 = new Station("서울대입구역");

        // when & then
        assertThatCode(() -> path.createPath(서울대입구역, 서울대입구역2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("출발점과 도착역이 같다면, 길을 생성할 수 없습니다.");
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
        Path path = new Path(List.of(section,section2));
        assertThatCode(() -> path.createPath(서울대입구역, 오이도역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("출발점과 도착역이 연결되어 있지 않습니다.");
    }

    @Test
    @DisplayName("존재하지 않은 출발역을 조회할 경우 에러")
    void 존재하지_않는_출발역_조회_할_경우_에러() {

        // given
        Station 서울대입구역 = new Station("서울대입구역");
        Station 신대방역 = new Station("신대방역");
        Station 대림역 = new Station("대림역");
        Station 신당역 = new Station("신당역");

        Section section = new Section(서울대입구역, 신대방역, new Distance(10));
        Path path = new Path(List.of(section));
        // when & then
        assertThatCode(() -> path.createPath(대림역, 신대방역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("출발역이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("존재하지 않은 도착역을 조회할 경우")
    void 존재하지_않는_도착역_조회_할_경우_에러() {

        // given
        Station 서울대입구역 = new Station("서울대입구역");
        Station 신대방역 = new Station("신대방역");
        Station 대림역 = new Station("대림역");

        Section section = new Section(서울대입구역, 신대방역, new Distance(10));
        Path path = new Path(List.of(section));
        // when & then
        assertThatCode(() -> path.createPath(서울대입구역, 대림역))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("도착역이 존재하지 않습니다.");
    }
}
