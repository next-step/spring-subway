package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.ErrorType;
import subway.exception.ServiceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionsTest {

    private Sections sections;
    private Section section1;
    private Section section2;
    private Section section3;


    @BeforeEach
    void setUp() {
        sections = new Sections();
        section1 = Section.builder()
                .upStationId(1L)
                .downStationId(2L)
                .build();
        section2 = Section.builder()
                .upStationId(2L)
                .downStationId(3L)
                .build();
        section3 = Section.builder()
                .upStationId(3L)
                .downStationId(4L)
                .build();
    }

    @DisplayName("등록된 구간이 없으면 구간을 추가할 수 있다.")
    @Test
    void addSection_empty() {
        // when
        sections.addSection(section1);

        // then
        assertThat(sections).isNotNull();
    }

    @DisplayName("추가하는 구간의 상행역이 기존 노선의 하행 종점이 아니면 등록 가능하다.")
    @Test
    void addSection() {
        // given
        int expected = 2;

        // when
        sections.addSection(section1);
        sections.addSection(section2);

        // then
        assertThat(sections.getValue().size()).isEqualTo(expected);
    }


    @DisplayName("추가하는 구간의 상행역이 기존 노선의 하행 종점이 아니면 등록 불가능하다.")
    @Test
    void validateConnectAbleStation() {
        // given
        sections.addSection(section1);

        // then
        assertThatThrownBy(() -> sections.addSection(section3))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ErrorType.VALIDATE_CONNECT_ABLE_STATION.getMessage());
    }


    @DisplayName("노선에 이미 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.")
    @Test
    void validateDuplicateSection() {
        // given
        long downStationId = 1L;
        Section section = Section.builder()
                .downStationId(downStationId)
                .build();
        sections.addSection(section);

        Section section2 = Section.builder()
                .upStationId(downStationId)
                .build();

        // then
        assertThatThrownBy(() -> sections.addSection(section2))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ErrorType.ALREADY_EXIST_SECTION.getMessage());
    }

    @DisplayName("마지막 구간을 삭제할 수 있다.")
    @Test
    void deleteSection() {
        // given
        int expected = 1;
        sections.addSection(section1);
        sections.addSection(section2);
        Station station = new Station(section2.getDownStationId());

        // when
        sections.deleteSection(station);

        // then
        assertThat(sections.getValue().size()).isEqualTo(expected);
    }

    @DisplayName("마지막 구간이 아니면 삭제 불가능하다.")
    @Test
    void validateDelete() {
        // given
        sections.addSection(section1);
        Station station = new Station(section1.getUpStationId());

        // then
        assertThatThrownBy(() -> sections.deleteSection(station))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ErrorType.VALIDATE_DELETE_SECTION.getMessage());
    }
}
