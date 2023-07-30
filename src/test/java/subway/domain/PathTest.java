package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.ErrorCode;
import subway.exception.IncorrectRequestException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

class PathTest {

    @DisplayName("경로를 탐색하여 최단거리를 반환한다.")
    @Test
    void pathFind() {
        // given
        Station gyodae = new Station(1L,"교대역");
        Station gangnam = new Station(2L,"강남역");
        Station yangjae = new Station(3L,"양재역");
        Station southTerminal = new Station(4L,"남부터미널역");

        Sections line2Sections = new Sections(List.of(
                new Section(gyodae, gangnam, 10)
        ));
        Sections sinBundangLineSections = new Sections(List.of(
                new Section(gangnam, yangjae, 10)
        ));
        Sections line3Sections = new Sections(List.of(
                new Section(gyodae, southTerminal, 2),
                new Section(southTerminal, yangjae, 3)
        ));

        List<Sections> allSections = List.of(line2Sections, sinBundangLineSections, line3Sections);

        // when
        Path path = new Path(allSections, gyodae, yangjae);

        // then
        assertThat(path.getPath()).containsExactly(gyodae, southTerminal, yangjae);
        assertThat(path.getDistance()).isEqualTo(new Distance(5));
    }

    @DisplayName("같은 역으로 경로 탐색을 하면 예외를 던진다.")
    @Test
    void sameStation() {
        // given
        Station gyodae = new Station(1L,"교대역");
        Station gangnam = new Station(2L,"강남역");

        Sections line2Sections = new Sections(List.of(
                new Section(gyodae, gangnam, 10)
        ));

        List<Sections> allSections = List.of(line2Sections);

        // when
        Exception exception = catchException(() -> new Path(allSections, gyodae, gyodae));

        // then
        assertThat(exception).isInstanceOf(IncorrectRequestException.class);
        assertThat(((IncorrectRequestException) exception).getErrorCode())
                .isEqualTo(ErrorCode.SAME_SOURCE_TARGET);
    }

    @DisplayName("출발역이 노선에 연결되어 있지 않을 때 경로 탐색을 하면 예외를 던진다.")
    @Test
    void notConnectedStart() {
        // given
        Station gyodae = new Station(1L,"교대역");
        Station gangnam = new Station(2L,"강남역");
        Station yangjae = new Station(3L,"양재역");
        Station southTerminal = new Station(4L,"남부터미널역");

        Sections line2Sections = new Sections(List.of(
                new Section(gyodae, gangnam, 10)
        ));
        Sections line3Sections = new Sections(List.of(
                new Section(gyodae, southTerminal, 2)
        ));

        List<Sections> allSections = List.of(line2Sections, line3Sections);

        // when
        Exception exception = catchException(() -> new Path(allSections, yangjae, gyodae));

        // then
        assertThat(exception).isInstanceOf(IncorrectRequestException.class);
        assertThat(((IncorrectRequestException) exception).getErrorCode())
                .isEqualTo(ErrorCode.NO_CONNECTED_PATH);
    }

    @DisplayName("도착역이 노선에 연결되어 있지 않을 때 경로 탐색을 하면 예외를 던진다.")
    @Test
    void notConnectedEnd() {
        // given
        Station gyodae = new Station(1L,"교대역");
        Station gangnam = new Station(2L,"강남역");
        Station yangjae = new Station(3L,"양재역");
        Station southTerminal = new Station(4L,"남부터미널역");

        Sections line2Sections = new Sections(List.of(
                new Section(gyodae, gangnam, 10)
        ));
        Sections line3Sections = new Sections(List.of(
                new Section(gyodae, southTerminal, 2)
        ));

        List<Sections> allSections = List.of(line2Sections, line3Sections);

        // when
        Exception exception = catchException(() -> new Path(allSections, gyodae, yangjae));

        // then
        assertThat(exception).isInstanceOf(IncorrectRequestException.class);
        assertThat(((IncorrectRequestException) exception).getErrorCode())
                .isEqualTo(ErrorCode.NO_CONNECTED_PATH);
    }

    @DisplayName("출발역 노선과 도착역 노선이 연결되어 있지 않을 때 경로 탐색을 하면 예외를 던진다.")
    @Test
    void notConnected() {
        // given
        Station gyodae = new Station(1L,"교대역");
        Station gangnam = new Station(2L,"강남역");
        Station yangjae = new Station(3L,"양재역");
        Station southTerminal = new Station(4L,"남부터미널역");

        Sections line2Sections = new Sections(List.of(
                new Section(gyodae, gangnam, 10)
        ));
        Sections line3Sections = new Sections(List.of(
                new Section(yangjae, southTerminal, 2)
        ));

        List<Sections> allSections = List.of(line2Sections, line3Sections);

        // when
        Exception exception = catchException(() -> new Path(allSections, gyodae, yangjae));

        // then
        assertThat(exception).isInstanceOf(IncorrectRequestException.class);
        assertThat(((IncorrectRequestException) exception).getErrorCode())
                .isEqualTo(ErrorCode.NO_CONNECTED_PATH);
    }
}
