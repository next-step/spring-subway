package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static subway.exception.ErrorCode.INVALID_COLOR_NAME_BLANK;
import static subway.exception.ErrorCode.INVALID_LINE_NAME_BLANK;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    @DisplayName("라인에 Station 을 제거하는 기능")
    void removeSection() {
        // given
        Section section1 = new Section(new Station("신대방역"), new Station("상도역"), 1);
        Section section2 = new Section(new Station("상도역"), new Station("서울역"), 1);

        Sections sections = new Sections(List.of(section1, section2));
        Line line = new Line("2호선", "yellow", sections);
        Station deleteStation = new Station("상도역");

        // when
        Line newLine = line.removeStation(deleteStation);
        // then
        assertThat(newLine.getSections().getSections()).containsAll((
            List.of(
                new Section(
                    new Station("신대방역"),
                    new Station("서울역"),
                    2
                )
            )
        ));
    }


    @Test
    @DisplayName("라인 이름이 공백이거나 null 일 경우, 에러를 던진다")
    void 라인_이름_공백_null_일_경우_에러() {
        assertAll(
            () -> assertThatCode(() -> new Line(null, "green"))
                .isInstanceOf(SubwayException.class)
                .hasMessage(INVALID_LINE_NAME_BLANK.getMessage()),
            () -> assertThatCode(() -> new Line("", "green"))
                .isInstanceOf(SubwayException.class)
                .hasMessage(INVALID_LINE_NAME_BLANK.getMessage())
        );
    }

    @Test
    @DisplayName("라인 색깔이 공백이거나 null 일 경우, 에러를 던진다")
    void 라인_색깔_공백_null_일_경우_에러() {
        assertAll(
            () -> assertThatCode(() -> new Line("2호선", null))
                .isInstanceOf(SubwayException.class)
                .hasMessage(INVALID_COLOR_NAME_BLANK.getMessage()),
            () -> assertThatCode(() -> new Line("2호선", ""))
                .isInstanceOf(SubwayException.class)
                .hasMessage(INVALID_COLOR_NAME_BLANK.getMessage())
        );
    }

}
