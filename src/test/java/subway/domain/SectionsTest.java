package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayException;

class SectionsTest {

    private final static Long LINE_ID = 1L;
    private final static Section FIRST_SECTION = new Section(1L, LINE_ID, 1L, 2L, 1L);
    private final static Section LAST_SECTION = new Section(4L, LINE_ID, 4L, 5L, 4L);
    private final static Sections SECTIONS = new Sections(List.of(
            FIRST_SECTION,
            new Section(2L, LINE_ID, 2L, 3L, 2L),
            new Section(3L, LINE_ID, 3L, 4L, 3L),
            LAST_SECTION
    ));

    @Test
    @DisplayName("Sections를 정상적으로 생성한다.")
    void create() {
        /* given */
        final List<Section> sections = List.of(
                new Section(LINE_ID, 1L, 2L, 1L),
                new Section(LINE_ID, 2L, 3L, 2L),
                new Section(LINE_ID, 3L, 4L, 3L)
        );

        /* when & then */
        assertDoesNotThrow(() -> new Sections(sections));
    }

    @Test
    @DisplayName("Sections를 빈 리스트로 생성하려는 경우 SubwayException을 던진다.")
    void createWithEmptySectionsThrowException() {
        List<Section> sections = Collections.emptyList();

        assertThatThrownBy(() -> new Sections(sections))
                .isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("Sections에 두 Section이 있는지 확인할 수 있다.")
    void containsBoth() {
        /* given */

        /* when & then */
        assertThat(SECTIONS.containsBoth(4L, 5L)).isTrue();
        assertThat(SECTIONS.containsBoth(5L, 6L)).isFalse();
        assertThat(SECTIONS.containsBoth(6L, 7L)).isFalse();
    }

    @Test
    @DisplayName("Sections에 가장 처음의 구간을 찾을 수 있다.")
    void getFirstSection() {
        /* given */

        /* when */
        final Section firstSection = SECTIONS.getFirstSection();

        /* then */
        assertThat(firstSection).isEqualTo(FIRST_SECTION);
    }

    @Test
    @DisplayName("Sections에 가장 마지막 구간을 찾을 수 있다.")
    void getLastSection() {
        /* given */

        /* when */
        final Section lastSection = SECTIONS.getLastSection();

        /* then */
        assertThat(lastSection).isEqualTo(LAST_SECTION);
    }

    @Test
    @DisplayName("Section의 크기가 1인지 확인할 수 있다.")
    void isEqualSizeToOne() {
        /* given */
        final Sections sections = new Sections(List.of(
                new Section(2L, 2L, 3L, 2L)
        ));

        /* when & then */
        assertThat(sections.isEqualSizeToOne()).isTrue();
    }

    @Test
    @DisplayName("현재 구간들 중 상행 종점역인지 확인한다.")
    void isFirstStation() {
        assertThat(SECTIONS.isFirstStation(FIRST_SECTION.getUpStationId())).isTrue();
        assertThat(SECTIONS.isFirstStation(5L)).isFalse();
    }

    @Test
    @DisplayName("현재 구간들 중 하행 종점역인지 확인한다.")
    void isLastStation() {
        assertThat(SECTIONS.isLastStation(LAST_SECTION.getDownStationId())).isTrue();
        assertThat(SECTIONS.isLastStation(1L)).isFalse();
    }

    @Test
    @DisplayName("하행 역이 같은 구간을 반환한다.")
    void getBetweenSectionToNext() {
        assertThat(SECTIONS.getBetweenSectionToNext(2L)).isEqualTo(FIRST_SECTION);
    }

    @Test
    @DisplayName("하행 역이 같은 구간이 없는 경우 SubwayException을 던진다.")
    void getBetweenSectionToNextWithException() {
        final Long isNotValidSectionId = 1234L;

        assertThatThrownBy(() -> SECTIONS.getBetweenSectionToNext(isNotValidSectionId))
                .isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("상행 역이 같은 구간을 반환한다.")
    void getBetweenSectionToPrev() {
        assertThat(SECTIONS.getBetweenSectionToPrev(4L)).isEqualTo(LAST_SECTION);
    }

    @Test
    @DisplayName("상행 역이 같은 구간이 없는 경우 SubwayException을 던진다.")
    void getBetweenSectionToPrevWithException() {
        final Long isNotValidSectionId = 1234L;

        assertThatThrownBy(() -> SECTIONS.getBetweenSectionToPrev(isNotValidSectionId))
                .isInstanceOf(SubwayException.class);
    }
}
