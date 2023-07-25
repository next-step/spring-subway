package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

public class LineTest {

    @Test
    @DisplayName("Line에 Section을 추가하는 기능.")
    void addSection() {
        // given
        Section section = new Section(
            new Station("서울대입구역"),
            new Station("신대방역"),
            10
        );
        Line line = new Line("2호선", "green", new Sections(List.of(section)));

        Station upStation = new Station("신대방역");
        Station downStation = new Station("잠실역");
        Section newSection = new Section(upStation, downStation, 10);

        // when
        Line addedLine = line.addSection(newSection);

        // then
        assertThat(addedLine.getSections().getSections()).contains(section);
    }


    @Test
    @DisplayName("라인 이름이 공백이거나 null 일 경우, 에러를 던진다")
    void 라인_이름_공백_null_일_경우_에러() {
        assertAll(
            () -> assertThatCode(() -> new Line(null, "green"))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.INVALID_LINE_NAME.getMessage()),
            () -> assertThatCode(() -> new Line("", "green"))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.INVALID_LINE_NAME.getMessage())
        );
    }

    @Test
    @DisplayName("라인 색깔이 공백이거나 null 일 경우, 에러를 던진다")
    void 라인_색깔_공백_null_일_경우_에러() {
        assertAll(
            () -> assertThatCode(() -> new Line("2호선", null))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.INVALID_COLOR_NAME.getMessage()),
            () -> assertThatCode(() -> new Line("2호선", ""))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.INVALID_COLOR_NAME.getMessage())
        );
    }
}
