package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.ErrorType;
import subway.exception.ServiceException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static subway.integration.StationStep.강남역;
import static subway.integration.StationStep.미금역;
import static subway.integration.StationStep.정자역;
import static subway.integration.StationStep.판교역;

class SectionsTest {

    private Sections sections;
    private Section section1;
    private Section section2;
    private Section section3;


    @BeforeEach
    void setUp() {
        Station 강남 = new Station(1L, 강남역);
        Station 판교 = new Station(2L, 판교역);
        Station 정자 = new Station(3L, 정자역);
        Station 미금 = new Station(4L, 미금역);

        sections = new Sections();
        section1 = Section.builder()
                .upStation(강남)
                .downStation(판교)
                .build();
        section2 = Section.builder()
                .upStation(판교)
                .downStation(정자)
                .build();
        section3 = Section.builder()
                .upStation(정자)
                .downStation(미금)
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
        sections.addSection(section1);
        sections.addSection(section2);

        Section section = Section.builder()
                .upStation(section2.getDownStation())
                .downStation(section1.getUpStation())
                .build();

        // then
        assertThatThrownBy(() -> sections.addSection(section))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ErrorType.ALREADY_EXIST_SECTION.getMessage());
    }

    @DisplayName("마지막 구간을 삭제할 수 있다.")
    @Test
    void deleteLastSection() {
        // given
        int expected = 1;
        sections.addSection(section1);
        sections.addSection(section2);
        Station station = new Station(section2.getDownStationId());

        // when
        sections.deleteLastSection(station);

        // then
        assertThat(sections.getValue().size()).isEqualTo(expected);
    }

    @DisplayName("등록된 구간이 없는 상태에서 구간을 삭제하는 경우")
    @Test
    void deleteLastSection_empty() {
        // given
        int expected = 0;
        Station station = new Station(section1.getDownStationId());

        // when
        sections.deleteLastSection(station);

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
        assertThatThrownBy(() -> sections.deleteLastSection(station))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ErrorType.VALIDATE_DELETE_SECTION.getMessage());
    }
}
