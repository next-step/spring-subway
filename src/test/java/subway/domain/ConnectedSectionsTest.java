package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayIllegalArgumentException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class ConnectedSectionsTest {

    ConnectedSections connectedSections;
    Section section1, section2, section3;

    @BeforeEach
    void setUp() {
        section1 = new Section(11L, 1L, 11L, 12L, 777L);
        section2 = new Section(12L, 1L, 12L, 13L, 777L);
        section3 = new Section(13L, 1L, 13L, 14L, 777L);

        connectedSections = new ConnectedSections(List.of(section1, section2, section3));
    }

    @Test
    @DisplayName("연결된 구간을 생성한다.")
    void createConnectedSections() {
        /* given */
        final List<Section> sections = List.of(section1, section2, section3);

        /* when & then */
        assertDoesNotThrow(() -> new ConnectedSections(sections));
    }

    @Test
    @DisplayName("구간들이 모두 같은 노선이 아닌 경우 생성 시 SubwayIllegalArgumentException을 던진다.")
    void createFailWithNotAllSameLine() {
        /* given */
        final List<Section> sections = List.of(
                section1,
                section2,
                new Section(21L, 2L, 21L, 24L, 777L)
        );

        /* when & then */
        assertThatThrownBy(() -> new ConnectedSections(sections)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("구간은 모두 같은 노선에 있어야합니다.");
    }

    @Test
    @DisplayName("구간이 존재하지 않는 경우 생성 시 SubwayIllegalArgumentException을 던진다.")
    void createFailWithNotExistSection() {
        /* given */
        final List<Section> sections = Collections.emptyList();

        /* when & then */
        assertThatThrownBy(() -> new ConnectedSections(sections)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("구간은 최소 하나 이상 있어야합니다.");
    }

    @Test
    @DisplayName("구간들이 서로 이어지지 않은 경우 생성 시 SubwayIllegalArgumentException을 던진다.")
    void createFailWithNotConnectedSections() {
        /* given */
        final List<Section> sections = List.of(section1, section3);

        /* when & then */
        assertThatThrownBy(() -> new ConnectedSections(sections)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("구간이 모두 연결되어있지 않습니다.");
    }

    @Test
    @DisplayName("구간들을 상행 종점역부터 하행 종점역까지의 순서로 저장한다.")
    void checkOrder() {
        /* given */
        final List<Section> randomOrderSections = List.of(section3, section1, section2);

        /* when */
        final ConnectedSections result = new ConnectedSections(randomOrderSections);

        /* then */
        assertIterableEquals(result.getConnectedSections(), List.of(section1, section2, section3));
    }

    @Test
    @DisplayName("새로운 구간의 모든 역이 이미 기존 구간에 있을 경우 생성 시 SubwayIllegalArgumentException을 던진다.")
    void addFailWithBothExistStationInSections() {
        /* given */
        final Section section = new Section(14L, 1L, 11L, 14L, 777L);

        /* when & then */
        assertThatThrownBy(() -> connectedSections.add(section)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("상행 역과 하행 역이 이미 노선에 모두 등록되어 있습니다.");
    }

    @Test
    @DisplayName("새로운 구간의 역 중 하나도 기존 구간에 없을 경우 생성 시 SubwayIllegalArgumentException을 던진다.")
    void addFailWithNotExistStationInSections() {
        /* given */
        final Section section = new Section(14L, 1L, 1234L, 4321L, 777L);

        /* when & then */
        assertThatThrownBy(() -> connectedSections.add(section)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("상행 역과 하행 역이 모두 노선에 없습니다.");
    }

    @Test
    @DisplayName("상행 종점역 앞에 구간을 추가한다.")
    void addSectionInFrontOfFirstSection() {
        /* given */
        final Section target = new Section(14L, 1L, 10L, 11L, 777L);

        /* when */
        SectionEditResult sectionEditResult = connectedSections.add(target);

        /* then */
        assertThat(sectionEditResult.getAddedSections()).isEqualTo(List.of(target));
        assertThat(sectionEditResult.getRemovedSections()).isEqualTo(Collections.emptyList());
        assertIterableEquals(
                List.of(
                        new Section(14L, 1L, 10L, 11L, 777L),
                        section1,
                        section2,
                        section3
                ),
                connectedSections.getConnectedSections()
        );
    }

    @Test
    @DisplayName("하행 종점역 뒤에 구간을 추가한다.")
    void addSectionBehindLastSection() {
        /* given */
        final Section target = new Section(14L, 1L, 14L, 15L, 777L);

        /* when */
        SectionEditResult sectionEditResult = connectedSections.add(target);

        /* then */
        assertThat(sectionEditResult.getAddedSections()).isEqualTo(List.of(target));
        assertThat(sectionEditResult.getRemovedSections()).isEqualTo(Collections.emptyList());
        assertIterableEquals(
                List.of(
                        section1,
                        section2,
                        section3,
                        new Section(14L, 1L, 14L, 15L, 777L)
                ),
                connectedSections.getConnectedSections()
        );
    }

    @Test
    @DisplayName("기존 구간에 추가하는 구간의 길이가 더 길경우 SubwayIllegalArgumentException을 던진다.")
    void addFailWithLongerSectionThanExistSection() {
        /* given */
        final Section section = new Section(14L, 1L, 11L, 17L, 12345L);

        /* when & then */
        assertThatThrownBy(() -> connectedSections.add(section)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("새로운 구간의 길이는 기존 구간의 길이보다 짧아야 합니다.");
    }

    @Test
    @DisplayName("기존 구간의 상행역 뒤에 구간을 추가한다.")
    void addSectionBehindUpStationOfExistSection() {
        /* given */
        final Section target = new Section(14L, 1L, 11L, 17L, 77L);

        /* when */
        final SectionEditResult sectionEditResult = connectedSections.add(target);

        /* then */
        assertThat(sectionEditResult.getAddedSections()).isEqualTo(
                List.of(
                        target,
                        new Section(null, 1L, 17L, 12L, 700L)
                )
        );
        assertThat(sectionEditResult.getRemovedSections()).isEqualTo(
                List.of(
                        new Section(11L, 1L, 11L, 12L, 777L)
                )
        );
        assertIterableEquals(
                List.of(
                        target,
                        new Section(null, 1L, 17L, 12L, 700L),
                        new Section(12L, 1L, 12L, 13L, 777L),
                        new Section(13L, 1L, 13L, 14L, 777L)
                ),
                connectedSections.getConnectedSections()
        );
    }

    @Test
    @DisplayName("기존 구간의 하행역 앞에 구간을 추가한다.")
    void addSectionInFrontOfDownStationOfExistSection() {
        /* given */
        final Section target = new Section(14L, 1L, 17L, 12L, 77L);

        /* when */
        final SectionEditResult sectionEditResult = connectedSections.add(target);

        /* then */
        assertThat(sectionEditResult.getAddedSections()).isEqualTo(
                List.of(
                        new Section(null, 1L, 11L, 17L, 700L),
                        target
                )
        );
        assertThat(sectionEditResult.getRemovedSections()).isEqualTo(
                List.of(
                        new Section(11L, 1L, 11L, 12L, 777L)
                )
        );
        assertIterableEquals(
                List.of(
                        new Section(null, 1L, 11L, 17L, 700L),
                        target,
                        new Section(12L, 1L, 12L, 13L, 777L),
                        new Section(13L, 1L, 13L, 14L, 777L)
                ),
                connectedSections.getConnectedSections()
        );
    }

    @Test
    @DisplayName("하행 종점역이 포함된 구간을 삭제할 수 있다.")
    void removeLastStationSection() {
        /* given */
        final Long stationId = 14L;

        /* when */
        SectionEditResult sectionEditResult = connectedSections.remove(stationId);

        /* then */
        assertThat(sectionEditResult.getAddedSections()).isEqualTo(Collections.emptyList());
        assertThat(sectionEditResult.getRemovedSections()).isEqualTo(
                List.of(
                        new Section(13L, 1L, 13L, 14L, 777L)
                )
        );
    }


    @Test
    @DisplayName("노선에 구간이 하나만 존재하는 경우 삭제 시 SubwayIllegalArgumentException을 던진다.")
    void removeFailWithOnlyOneSection() {
        /* given */
        final ConnectedSections sections = new ConnectedSections(List.of(section1));

        /* when & then */
        assertThatThrownBy(() -> sections.remove(11L)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("해당 노선에 구간이 하나여서 제거할 수 없습니다.");
    }

    @Test
    @DisplayName("삭제하려는 역이 전체 구간에 존재하지 않는 역인 경우 삭제 시 SubwayIllegalException을 던진다.")
    void removeFailWithDoesNotExistStationInConnectedSections() {
        /* given */
        final Long stationId = 999L;

        /* when & then */
        assertThatThrownBy(() -> connectedSections.remove(stationId)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("삭제하려는 역이 전체 구간에 존재하지 않습니다. 삭제하려는 역: 999");
    }
}
