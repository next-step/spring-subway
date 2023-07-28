package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayException;

class LineWithSectionsTest {

    private static final Long LINE_ID = 1L;
    private static final Long DIFF_LINE_ID = 2L;
    private static final String LINE_NAME = "2호선";
    private static final String LINE_COLOR = "#00FF00";
    private static final Line LINE = new Line(LINE_ID, LINE_NAME, LINE_COLOR);
    private static final Line DIFF_LINE = new Line(DIFF_LINE_ID, LINE_NAME, LINE_COLOR);

    private static final List<LineWithSection> LINE_WITH_SECTIONS_LIST = List.of(
            new LineWithSection(LINE, new Section(1L, LINE_ID, 1L, 2L, 1L)),
            new LineWithSection(LINE, new Section(2L, LINE_ID, 2L, 3L, 1L)),
            new LineWithSection(LINE, new Section(3L, LINE_ID, 3L, 4L, 1L))
    );

    @Test
    @DisplayName("노선과 구간을 같이 생성한다.")
    void create() {
        Assertions.assertDoesNotThrow(() -> new LineWithSections(LINE_WITH_SECTIONS_LIST));
    }

    @Test
    @DisplayName("노선을 가져온다.")
    void getLine() {
        final LineWithSections lineWithSections = new LineWithSections(LINE_WITH_SECTIONS_LIST);

        assertThat(lineWithSections.getLine()).isEqualTo(LINE);
    }

    @Test
    @DisplayName("상행 종점역에서 하행 종점역까지 정렬된 역의 아이디를 가져온다.")
    void getSortedStationIds() {
        final LineWithSections lineWithSections = new LineWithSections(LINE_WITH_SECTIONS_LIST);

        assertThat(lineWithSections.getSortedStationIds()).containsExactly(1L, 2L, 3L, 4L);
    }

    @Test
    @DisplayName("다른 노선의 구간이 포함된 경우 SubwayException을 던진다.")
    void validatesSectionHasSameLine() {
        final List<LineWithSection> lineWithSectionList = List.of(
                new LineWithSection(LINE, new Section(1L, LINE_ID, 1L, 2L, 1L)),
                new LineWithSection(DIFF_LINE, new Section(2L, DIFF_LINE_ID, 2L, 3L, 1L))
        );

        assertThatThrownBy(() -> new LineWithSections(lineWithSectionList)).isInstanceOf(SubwayException.class);
    }
}