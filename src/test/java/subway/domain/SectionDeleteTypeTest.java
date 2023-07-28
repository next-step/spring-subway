package subway.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.exception.SectionDeleteException;

@DisplayName("구간 제거 타입 단위 테스트")
class SectionDeleteTypeTest {

    private final Line line = new Line(1L, "1호선", "green");
    private final Station station1 = new Station(1L, "낙성대");
    private final Station station2 = new Station(2L, "사당");
    private final Station station3 = new Station(3L, "이수");
    private final Distance d10 = new Distance(10L);
    private final Section upSection = new Section(line, station1, station2, d10);
    private final Section downSection = new Section(line, station2, station3, d10);

    @Nested
    @DisplayName("of 메소드 테스트")
    class WhenOf {

        @DisplayName("노선이 빈경우 EMPTY_LINE 반환")
        @Test
        void givenEmptyLineThenReturnEMPTY_LINE() {
            // given
            Sections sections = new Sections(List.of());
            boolean isUpSection = false;
            boolean isDownSection = false;

            // when
            SectionDeleteType type = SectionDeleteType.of(sections.getSections().size(),
                    isUpSection,
                    isDownSection);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.EMPTY_LINE);
        }

        @DisplayName("노선에 지울 역이 없는 경우 NO_STATION 반환")
        @Test
        void givenNoStationInLineThenReturnNO_STATION() {
            // given
            Sections sections = new Sections(List.of(upSection, downSection));
            boolean isUpSection = false;
            boolean isDownSection = false;

            // when
            SectionDeleteType type = SectionDeleteType.of(sections.getSections().size(),
                    isUpSection,
                    isDownSection);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.NO_STATION);
        }

        @DisplayName("노선이 너무 짧은 경우 SHORT_LINE 반환")
        @Test
        void givenTooShortLineThenReturnSHORT_LINE() {
            // given
            Sections sections = new Sections(List.of(upSection));
            boolean isUpSection = true;
            boolean isDownSection = false;

            // when
            SectionDeleteType type = SectionDeleteType.of(sections.getSections().size(),
                    isUpSection,
                    isDownSection);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.SHORT_LINE);
        }

        @DisplayName("노선 중간에 있는 역일시 MIDDLE_STATION 반환")
        @Test
        void givenMiddleStationThenReturnMIDDLE_STATION() {
            // given
            Sections sections = new Sections(List.of(upSection, downSection));
            boolean isUpSection = true;
            boolean isDownSection = true;

            // when
            SectionDeleteType type = SectionDeleteType.of(sections.getSections().size(),
                    isUpSection,
                    isDownSection);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.MIDDLE_STATION);
        }

        @DisplayName("노선 중간에 있는 역일시 FIRST_STATION 반환")
        @Test
        void givenFirstStationThenReturnFIRST_STATION() {
            // given
            Sections sections = new Sections(List.of(upSection, downSection));
            boolean isUpSection = false;
            boolean isDownSection = true;

            // when
            SectionDeleteType type = SectionDeleteType.of(sections.getSections().size(),
                    isUpSection,
                    isDownSection);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.FIRST_STATION);
        }

        @DisplayName("노선 중간에 있는 역일시 LAST_STATION 반환")
        @Test
        void givenLastStationThenReturnLAST_STATION() {
            // given
            Sections sections = new Sections(List.of(upSection, downSection));
            boolean isUpSection = true;
            boolean isDownSection = false;

            // when
            SectionDeleteType type = SectionDeleteType.of(sections.getSections().size(),
                    isUpSection,
                    isDownSection);

            // then
            assertThat(type).isEqualTo(SectionDeleteType.LAST_STATION);
        }
    }

    @Nested
    @DisplayName("findDeleteSections 메소드 테스트")
    class WhenFindDeleteSections {

        @DisplayName("EMPTY_LINE 예외발생")
        @Test
        void givenEMPTY_LINEThenThrow() {
            // when, then
            assertThatCode(() -> SectionDeleteType.EMPTY_LINE.
                    findDeleteSections(upSection, downSection))
                    .isInstanceOf(SectionDeleteException.class);
        }

        @DisplayName("NO_STATION 예외발생")
        @Test
        void givenNO_STATIONThenThrow() {
            // when, then
            assertThatCode(() -> SectionDeleteType.NO_STATION.
                    findDeleteSections(upSection, downSection))
                    .isInstanceOf(SectionDeleteException.class);
        }

        @DisplayName("SHORT_LINE 예외발생")
        @Test
        void givenSHORT_LINEThenThrow() {
            // when, then
            assertThatCode(() -> SectionDeleteType.SHORT_LINE.
                    findDeleteSections(upSection, downSection))
                    .isInstanceOf(SectionDeleteException.class);
        }

        @DisplayName("MIDDLE_STATION 경우 양옆 구간 반환")
        @Test
        void givenMIDDLE_STATIONThenReturnBoth() {
            // when
            List<Section> deleteSections = SectionDeleteType.MIDDLE_STATION
                    .findDeleteSections(upSection, downSection);

            // then
            assertThat(deleteSections).contains(upSection, downSection);
        }

        @DisplayName("FIRST_STATION 경우 downSection 반환")
        @Test
        void givenFIRST_STATIONThenReturnDown() {
            // when
            List<Section> deleteSections = SectionDeleteType.FIRST_STATION
                    .findDeleteSections(upSection, downSection);

            // then
            assertThat(deleteSections).contains(downSection);
        }

        @DisplayName("LAST_STATION 경우 upSection 반환")
        @Test
        void givenLAST_STATIONThenReturnUp() {
            // when
            List<Section> deleteSections = SectionDeleteType.LAST_STATION
                    .findDeleteSections(upSection, downSection);

            // then
            assertThat(deleteSections).contains(upSection);
        }
    }

    @Nested
    @DisplayName("findCombinedSection 메소드 테스트")
    class WhenFindCombinedSection {

        @DisplayName("EMPTY_LINE 예외발생")
        @Test
        void givenEMPTY_LINEThenThrow() {
            // when, then
            assertThatCode(() -> SectionDeleteType.EMPTY_LINE.
                    findCombinedSection(upSection, downSection))
                    .isInstanceOf(SectionDeleteException.class);
        }

        @DisplayName("NO_STATION 예외발생")
        @Test
        void givenNO_STATIONThenThrow() {
            // when, then
            assertThatCode(() -> SectionDeleteType.NO_STATION.
                    findCombinedSection(upSection, downSection))
                    .isInstanceOf(SectionDeleteException.class);
        }

        @DisplayName("SHORT_LINE 예외발생")
        @Test
        void givenSHORT_LINEThenThrow() {
            // when, then
            assertThatCode(() -> SectionDeleteType.SHORT_LINE.
                    findCombinedSection(upSection, downSection))
                    .isInstanceOf(SectionDeleteException.class);
        }

        @DisplayName("MIDDLE_STATION 경우 합쳐진 구간 반환")
        @Test
        void givenMiddleStationThenReturnMIDDLE_STATION() {
            // when
            Optional<Section> combinedSection = SectionDeleteType.MIDDLE_STATION
                    .findCombinedSection(upSection, downSection);

            // then
            assertThat(combinedSection).isNotEmpty();
        }

        @DisplayName("FIRST_STATION 경우 empty")
        @Test
        void givenFirstStationThenReturnFIRST_STATION() {
            // when
            Optional<Section> combinedSection = SectionDeleteType.FIRST_STATION
                    .findCombinedSection(upSection, downSection);

            // then
            assertThat(combinedSection).isEmpty();
        }

        @DisplayName("LAST_STATION 경우 empty")
        @Test
        void givenLastStationThenReturnLAST_STATION() {
            // when
            Optional<Section> combinedSection = SectionDeleteType.LAST_STATION
                    .findCombinedSection(upSection, downSection);

            // then
            assertThat(combinedSection).isEmpty();
        }
    }

}
