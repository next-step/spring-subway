package subway.domain;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathGraphTest {

    @Test
    @DisplayName("Graph 일급 컬렉션을 만든다")
    void graph_객체_정상_생성() {
        // given
        Station 서울역 = new Station("서울역");
        Station 오이도역 = new Station("오이도역");
        Section section = new Section(서울역, 오이도역, new Distance(10));
        List<Section> sections = List.of(section);

        // when &then
        assertThatCode(() -> new PathGraph(sections))
            .doesNotThrowAnyException();
    }
    
}
