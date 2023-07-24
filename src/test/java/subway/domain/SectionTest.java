package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SectionCreateException;

class SectionTest {

    Line line;
    Station upStation;
    Station downStation;
    Distance distance;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "1호선", "green");
        upStation = new Station(1L, "낙성대");
        downStation = new Station(2L, "사당");
        distance = new Distance(10L);

    }

    @DisplayName("구간 정상 생성")
    @Test
    void validationSuccess() {
        // given, when, then
        assertThatCode(() -> new Section(line, upStation, downStation, distance))
                .doesNotThrowAnyException();
    }

    @DisplayName("노선 아이디가 null 일시 구간 생성 실패")
    @Test
    void validationLineIdNotNull() {
        // given, when, then
        assertThatCode(() -> new Section(null,
                upStation, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선은 null일 수 없습니다.");
    }

    @DisplayName("상행역 아이디가 null 일시 구간 생성 실패")
    @Test
    void validationUpStationIdNotNull() {
        // given , when, then
        assertThatCode(() -> new Section(
                line, null, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역 null일 수 없습니다.");
    }

    @DisplayName("하행역 아이디가 null 일시 구간 생성 실패")
    @Test
    void validationDownStationIdNotNull() {
        // given,  when, then
        assertThatCode(() -> new Section(
                line, upStation, null, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행역 null일 수 없습니다.");
    }

    @DisplayName("거리가 null 일시 구간 생성 실패")
    @Test
    void validationDistanceNotNull() {
        // given , when, then
        assertThatCode(() ->
                new Section(line, upStation, downStation, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 null일 수 없습니다.");
    }

    @DisplayName("상행역 아이디와 하행역 아이디는 같을 시 구간 생성 실패")
    @Test
    void validationUpStationIdNotEqualDownStationId() {
        // given, when, then
        assertThatCode(() ->
                new Section(line, upStation, upStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 같을 수 없습니다.");
    }

    @DisplayName("삽입할 구간을 가지고 기존 구간에서 업데이트할 구간을 생성 - 상행역 같은 경우")
    @Test
    void givenSameUpStationWhenCuttedSectionThenReturn() {
        // given

        Station station = new Station(3L, "잠실");
        Section section1 = new Section(line, upStation, downStation, new Distance(10L));
        Section section2 = new Section(line, upStation, station, new Distance(6L));

        // when
        Section result = section1.cuttedSection(section2);

        // then
        assertThat(result).extracting(
                Section::getLine,
                Section::getUpStation,
                Section::getDownStation,
                Section::getDistance
        ).contains(line, station, downStation, 4L);
    }

    @DisplayName("삽입할 구간을 가지고 기존 구간에서 업데이트할 구간을 생성 - 하행역 같은 경우")
    @Test
    void givenSameDownStationWhenCuttedSectionThenReturn() {
        // given

        Station station = new Station(3L, "잠실");
        Section section1 = new Section(line, upStation, downStation, new Distance(10L));
        Section section2 = new Section(line, station, downStation, new Distance(6L));

        // when
        Section result = section1.cuttedSection(section2);

        // then
        assertThat(result).extracting(
                Section::getLine,
                Section::getUpStation,
                Section::getDownStation,
                Section::getDistance
        ).contains(line, upStation, station, 4L);
    }

    @DisplayName("기존 구간보다 삽입할 구간이 더 길거나 길이가 같으면 예외를 던진다")
    @Test
    void givenInvalidDistanceWhenCuttedSectionThenThrow() {
        // given

        Station station = new Station(3L, "잠실");
        Section section1 = new Section(line, upStation, downStation, new Distance(10L));
        Section section2 = new Section(line, station, downStation, new Distance(10L));

        // when, then
        assertThatCode(() -> section1.cuttedSection(section2))
                .isInstanceOf(SectionCreateException.class)
                .hasMessage("역사이에 역 등록시 구간이 기존 구간보다 작아야합니다.");
    }

    @DisplayName("상행역과 하행역이 전부 다르면 예외를 던진다")
    @Test
    void givenDifferentStationsWhenCuttedSectionThenThrow() {
        // given

        Station station1 = new Station(3L, "잠실");
        Station station2 = new Station(4L, "잠실새내");
        Section section1 = new Section(line, upStation, downStation, new Distance(10L));
        Section section2 = new Section(line, station1, station2, new Distance(1L));

        // when, then
        assertThatCode(() -> section1.cuttedSection(section2))
                .isInstanceOf(SectionCreateException.class)
                .hasMessage("상행역과 하행역 중 하나는 같아야 합니다.");
    }
}
