package subway.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.exception.SectionCreateException;

@DisplayName("구간 생성 타입 단위 테스트")
class SectionCreateTypeTest {

    private final Line line = new Line(1L, "1호선", "green");
    private final Station station1 = new Station(1L, "낙성대");
    private final Station station2 = new Station(2L, "사당");
    private final Station station3 = new Station(3L, "이수");
    private final Station station4 = new Station(4L, "잠실");
    private final Station station5 = new Station(5L, "잠실새내");
    private final Distance d10 = new Distance(10L);
    private final Distance d6 = new Distance(6L);

    @Nested
    @DisplayName("of 메소드 테스트")
    class whenOf {

        @DisplayName("노선에 구간이 없을 시 NO_SECTION_IN_LINE 반환")
        @Test
        void givenEmptyLineWhenOfThenNO_SECTION_IN_LINE() {
            // given
            List<Section> sectionList = List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10),
                    new Section(line, station3, station4, d10)
            );
            Sections sections = new Sections(List.of());

            // when
            SectionCreateType type = SectionCreateType.of(sections, station1, station2);

            // then
            assertThat(type).isEqualTo(SectionCreateType.NO_SECTION_IN_LINE);
        }

        @DisplayName("추가구간의 하행역이 노선의 상행 종점역일시 ADD_FIRST_STATION 반환")
        @Test
        void givenFirstStationWhenOfThenADD_FIRST_STATION() {
            // given
            List<Section> sectionList = List.of(
                    new Section(line, station1, station2, d10)
            );
            Sections sections = new Sections(sectionList);

            // when
            SectionCreateType type = SectionCreateType.of(sections, station3, station1);

            // then
            assertThat(type).isEqualTo(SectionCreateType.ADD_FIRST_STATION);
        }

        @DisplayName("추가구간의 상행역이 노선의 하행 종점역일시 ADD_LAST_STATION 반환")
        @Test
        void givenLastStationWhenOfThenADD_LAST_STATION() {
            // given
            List<Section> sectionList = List.of(
                    new Section(line, station1, station2, d10)
            );
            Sections sections = new Sections(sectionList);

            // when
            SectionCreateType type = SectionCreateType.of(sections, station2, station3);

            // then
            assertThat(type).isEqualTo(SectionCreateType.ADD_LAST_STATION);
        }

        @DisplayName("추가구간의 상행역이 노선의 상행역과 일치하는 구간이 있을시 ADD_MIDDLE_WITH_UP_STATION 반환")
        @Test
        void givenMiddleStationWhenOfThenADD_MIDDLE_WITH_UP_STATION() {
            // given
            List<Section> sectionList = List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            );
            Sections sections = new Sections(sectionList);

            // when
            SectionCreateType type = SectionCreateType.of(sections, station2, station4);

            // then
            assertThat(type).isEqualTo(SectionCreateType.ADD_MIDDLE_WITH_UP_STATION);
        }

        @DisplayName("추가구간의 상행역이 노선의 상행역과 일치하는 구간이 있을시 ADD_MIDDLE_WITH_DOWN_STATION 반환")
        @Test
        void givenMiddleStationWhenOfThenADD_MIDDLE_WITH_DOWN_STATION() {
            // given
            List<Section> sectionList = List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            );
            Sections sections = new Sections(sectionList);

            // when
            SectionCreateType type = SectionCreateType.of(sections, station4, station2);

            // then
            assertThat(type).isEqualTo(SectionCreateType.ADD_MIDDLE_WITH_DOWN_STATION);
        }

        @DisplayName("추가구간의 역들이 노선에 없을시 BOTH_STATION_NOT_EXIST 반환")
        @Test
        void givenNoStationWhenOfThenBOTH_STATION_NOT_EXIST() {
            // given
            List<Section> sectionList = List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            );
            Sections sections = new Sections(sectionList);

            // when
            SectionCreateType type = SectionCreateType.of(sections, station4, station5);

            // then
            assertThat(type).isEqualTo(SectionCreateType.BOTH_STATION_NOT_EXIST);
        }

        @DisplayName("추가구간의 역들이 노선에 둘다 있을시 BOTH_STATION_EXIST 반환")
        @Test
        void givenBothStationWhenOfThenBOTH_STATION_EXIST() {
            // given
            List<Section> sectionList = List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            );
            Sections sections = new Sections(sectionList);

            // when
            SectionCreateType type = SectionCreateType.of(sections, station1, station2);

            // then
            assertThat(type).isEqualTo(SectionCreateType.BOTH_STATION_EXIST);
        }
    }

    @Nested
    @DisplayName("cutSection 메소드 테스트")
    class whenCutSection {

        @DisplayName("노선에 구간이 없을시 업데이트될 구간 없음")
        @Test
        void givenNO_SECTION_IN_LINE_ThenEmpty() {
            // given
            Sections sections = new Sections(List.of());

            // when
            Optional<Section> cutSection = SectionCreateType.NO_SECTION_IN_LINE
                    .cutSection(sections, station1, station2, d6);

            // then
            assertThat(cutSection).isEmpty();
        }

        @DisplayName("상행 종점역일시 업데이트될 구간 없음")
        @Test
        void givenADD_FIRST_STATION_ThenEmpty() {
            // given
            Sections sections = new Sections(List.of(new Section(line, station2, station3, d10)));

            // when
            Optional<Section> cutSection = SectionCreateType.ADD_FIRST_STATION
                    .cutSection(sections, station1, station2, d6);

            // then
            assertThat(cutSection).isEmpty();
        }

        @DisplayName("하행 종점역일시 업데이트될 구간 없음")
        @Test
        void givenADD_LAST_STATION_ThenEmpty() {
            // given
            Sections sections = new Sections(List.of(new Section(line, station2, station3, d10)));

            // when
            Optional<Section> cutSection = SectionCreateType.ADD_LAST_STATION
                    .cutSection(sections, station3, station1, d6);

            // then
            assertThat(cutSection).isEmpty();
        }

        @DisplayName("추가구간의 상행역이 노선에 있을시 업데이트될 구간 반환")
        @Test
        void givenADD_MIDDLE_WITH_UP_STATION_ThenReturn() {
            // given
            Sections sections = new Sections(List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            ));

            // when
            Optional<Section> cutSection = SectionCreateType.ADD_MIDDLE_WITH_UP_STATION
                    .cutSection(sections, station2, station4, d6);

            // then
            assertThat(cutSection).isNotEmpty();
        }

        @DisplayName("추가구간의 하행역이 노선에 있을시 업데이트될 구간 반환")
        @Test
        void givenADD_MIDDLE_WITH_DOWN_STATION_ThenReturn() {
            // given
            Sections sections = new Sections(List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            ));

            // when
            Optional<Section> cutSection = SectionCreateType.ADD_MIDDLE_WITH_DOWN_STATION
                    .cutSection(sections, station4, station2, d6);

            // then
            assertThat(cutSection).isNotEmpty();
        }

        @DisplayName("추가구간의 역이 노선에 없을시 예외 발생")
        @Test
        void givenBOTH_STATION_NOT_EXIST_ThenThrow() {
            // given
            Sections sections = new Sections(List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            ));

            // when, then
            assertThatCode(() -> SectionCreateType.BOTH_STATION_NOT_EXIST
                    .cutSection(sections, station4, station5, d6))
                    .isInstanceOf(SectionCreateException.class)
                    .hasMessage("추가할 구간의 하행역과 상행역이 기존 노선에 하나는 존재해야합니다.");
        }

        @DisplayName("추가구간의 역이 노선에 둘다 있을시 예외 발생")
        @Test
        void givenBOTH_STATION_EXIST_ThenThrow() {
            // given
            Sections sections = new Sections(List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            ));

            // when, then
            assertThatCode(() -> SectionCreateType.BOTH_STATION_EXIST
                    .cutSection(sections, station2, station3, d6))
                    .isInstanceOf(SectionCreateException.class)
                    .hasMessage("추가할 구간의 하행역과 상행역이 기존 노선에 모두 존재해서는 안됩니다.");
        }
    }
}
