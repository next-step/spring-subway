package subway.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.domain.vo.SectionDeleteVo;
import subway.exception.SectionDeleteException;

@DisplayName("구간 제거 타입 단위 테스트")
class SectionDeleteTypeTest {

    private final Line line = new Line(1L, "1호선", "green");
    private final Station station1 = new Station(1L, "낙성대");
    private final Station station2 = new Station(2L, "사당");
    private final Station station3 = new Station(3L, "이수");
    private final Distance d10 = new Distance(10L);
    private final Distance d6 = new Distance(6L);

    @Nested
    @DisplayName("of 메소드 테스트")
    class WhenOf {

        @DisplayName("노선이 빈경우 EMPTY_LINE 반환")
        @Test
        void givenEmptyLineThenReturnEMPTY_LINE() {
            // given
            Sections sections = new Sections(List.of());

            // when
            SectionDeleteType type = SectionDeleteType.of(sections, 1L);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.EMPTY_LINE);
        }

        @DisplayName("노선에 지울 역이 없는 경우 NO_STATION 반환")
        @Test
        void givenNoStationInLineThenReturnNO_STATION() {
            // given
            Sections sections = new Sections(List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            ));

            // when
            SectionDeleteType type = SectionDeleteType.of(sections, 4L);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.NO_STATION);
        }

        @DisplayName("노선이 너무 짧은 경우 SHORT_LINE 반환")
        @Test
        void givenTooShortLineThenReturnSHORT_LINE() {
            // given
            Sections sections = new Sections(List.of(
                    new Section(line, station1, station2, d10)
            ));

            // when
            SectionDeleteType type = SectionDeleteType.of(sections, 1L);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.SHORT_LINE);
        }

        @DisplayName("노선 중간에 있는 역일시 MIDDLE_STATION 반환")
        @Test
        void givenMiddleStationThenReturnMIDDLE_STATION() {
            // given
            Sections sections = new Sections(List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            ));

            // when
            SectionDeleteType type = SectionDeleteType.of(sections, 2L);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.MIDDLE_STATION);
        }

        @DisplayName("노선 중간에 있는 역일시 FIRST_STATION 반환")
        @Test
        void givenFirstStationThenReturnFIRST_STATION() {
            // given
            Sections sections = new Sections(List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            ));

            // when
            SectionDeleteType type = SectionDeleteType.of(sections, 1L);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.FIRST_STATION);
        }

        @DisplayName("노선 중간에 있는 역일시 LAST_STATION 반환")
        @Test
        void givenLastStationThenReturnLAST_STATION() {
            // given
            Sections sections = new Sections(List.of(
                    new Section(line, station1, station2, d10),
                    new Section(line, station2, station3, d10)
            ));

            // when
            SectionDeleteType type = SectionDeleteType.of(sections, 3L);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.LAST_STATION);
        }
    }

    @Nested
    @DisplayName("deleteAndCombineSections 메소드 테스트")
    class WhenDeleteAndCombineSections {

        @DisplayName("EMPTY_LINE 경우 예외발생")
        @Test
        void givenEMPTY_LINE_ThenThrow() {
            // given
            Optional<Section> upSection = Optional.empty();
            Optional<Section> downSection = Optional.empty();

            // when, then
            assertThatCode(() -> SectionDeleteType.EMPTY_LINE
                    .deleteAndCombineSections(upSection, downSection))
                    .isInstanceOf(SectionDeleteException.class)
                    .hasMessage("노선에 존재하는 역이 없습니다.");
        }

        @DisplayName("NO_STATION 경우 예외발생")
        @Test
        void givenNO_STATION_ThenThrow() {
            // given
            Optional<Section> upSection = Optional.empty();
            Optional<Section> downSection = Optional.empty();

            // when, then
            assertThatCode(() -> SectionDeleteType.NO_STATION
                    .deleteAndCombineSections(upSection, downSection))
                    .isInstanceOf(SectionDeleteException.class)
                    .hasMessage("노선에 해당하는 역을 가진 구간이 없습니다.");
        }

        @DisplayName("SHORT_LINE 경우 예외발생")
        @Test
        void givenSHORT_LINE_ThenThrow() {
            // given
            Optional<Section> upSection = Optional.of(new Section(line, station1, station2, d6));
            Optional<Section> downSection = Optional.empty();

            // when, then
            assertThatCode(() -> SectionDeleteType.SHORT_LINE
                    .deleteAndCombineSections(upSection, downSection))
                    .isInstanceOf(SectionDeleteException.class)
                    .hasMessage("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }

        @DisplayName("MIDDLE_STATION 경우 지워질 구간 리스트에 앞뒤 구간이 있고 추가될 구간에 앞뒤구간이 합쳐진 구간이 반환")
        @Test
        void givenMIDDLE_STATION_ThenReturnBothSectionsAndCombinedSection() {
            // given
            Optional<Section> upSection = Optional.of(new Section(line, station1, station2, d6));
            Optional<Section> downSection = Optional.of(new Section(line, station2, station3, d10));

            // when
            SectionDeleteVo deleteVo = SectionDeleteType.MIDDLE_STATION
                    .deleteAndCombineSections(upSection, downSection);

            // then
            assertThat(deleteVo).extracting(
                    SectionDeleteVo::getDeleteSections,
                    SectionDeleteVo::getCombinedSection
            ).contains(
                    List.of(upSection.get(), downSection.get()),
                    Optional.of(new Section(line, station1, station3, new Distance(16L)))
            );
        }

        @DisplayName("FIRST_STATION 경우 뒤 구간만 지우도록 반환")
        @Test
        void givenFIRST_STATION_ThenReturnDownSection() {
            // given
            Optional<Section> upSection = Optional.empty();
            Optional<Section> downSection = Optional.of(new Section(line, station2, station3, d10));

            // when
            SectionDeleteVo deleteVo = SectionDeleteType.FIRST_STATION
                    .deleteAndCombineSections(upSection, downSection);

            // then
            assertThat(deleteVo).extracting(
                    SectionDeleteVo::getDeleteSections,
                    SectionDeleteVo::getCombinedSection
            ).contains(
                    List.of(downSection.get()),
                    Optional.empty()
            );
        }

        @DisplayName("LAST_STATION 경우 앞 구간만 지우도록 반환")
        @Test
        void givenLAST_STATION_ThenReturnUpSection() {
            // given
            Optional<Section> upSection = Optional.of(new Section(line, station1, station2, d6));
            Optional<Section> downSection = Optional.empty();

            // when
            SectionDeleteVo deleteVo = SectionDeleteType.LAST_STATION
                    .deleteAndCombineSections(upSection, downSection);

            // then
            assertThat(deleteVo).extracting(
                    SectionDeleteVo::getDeleteSections,
                    SectionDeleteVo::getCombinedSection
            ).contains(
                    List.of(upSection.get()),
                    Optional.empty()
            );
        }
    }

}
