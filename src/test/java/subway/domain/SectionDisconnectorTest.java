package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.DomainFixture;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("SectionDisconnector 클래스")
public class SectionDisconnectorTest {

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void beforeEach() {
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
        station4 = new Station(4L, "station4");
    }

    @Nested
    @DisplayName("findSectionDisconnector 메소드")
    class findSectionDisconnector_Method {

        @Test
        @DisplayName("baseSection이 상행 종점 구간이고 상행역이 requestStation과 일치하면 SectionDisconnector.UP을 반환한다")
        void Return_UP_When_Input_UP_Station() {
            // given
            Station requestStation = station1;
            Section baseSection = DomainFixture.Section.buildWithStations(requestStation, station2);
            Section section = DomainFixture.Section.buildWithStations(station2, station3);

            baseSection.connectDownSection(section);

            // when
            Optional<SectionDisconnector> result = SectionDisconnector.findSectionDisconnector(baseSection, requestStation);

            // then
            assertThat(result).isEqualTo(Optional.of(SectionDisconnector.UP));
        }

        @Test
        @DisplayName("baseSection이 상행 종점 구간이 아니고 상행역이 requestStation과 일치하면 SectionDisconnector.MIDDLE_DOWN을 반환한다")
        void Return_MIDDLE_DOWN_When_Input_MIDDLE_Station() {
            // given
            Station requestStation = station2;
            Section section = DomainFixture.Section.buildWithStations(station1, requestStation);
            Section baseSection = DomainFixture.Section.buildWithStations(requestStation, station3);
            section.connectDownSection(baseSection);

            // when
            Optional<SectionDisconnector> result = SectionDisconnector.findSectionDisconnector(baseSection, requestStation);

            // then
            assertThat(result).isEqualTo(Optional.of(SectionDisconnector.MIDDLE_DOWN));
        }

        @Test
        @DisplayName("baseSection이 하행 종점 구간이고 하행역이 requestStation과 일치하면 SectionDisconnector.DOWN을 반환한다")
        void Return_DOWN_When_Input_DOWN_Station() {
            // given
            Station requestStation = station3;
            Section section = DomainFixture.Section.buildWithStations(station1, station2);
            Section baseSection = DomainFixture.Section.buildWithStations(station2, requestStation);
            section.connectDownSection(baseSection);

            // when
            Optional<SectionDisconnector> result = SectionDisconnector.findSectionDisconnector(baseSection, requestStation);

            // then
            assertThat(result).isEqualTo(Optional.of(SectionDisconnector.DOWN));
        }
        @Test
        @DisplayName("일치하는 케이스가 없으면 null을 반환한다.")
        void Return_Null_When_Input_Invalid_Section() {
            // given
            Station requestStation = station4;
            Section baseSection = DomainFixture.Section.buildWithStations(station1, station2);

            // when
            Optional<SectionDisconnector> result = SectionDisconnector.findSectionDisconnector(baseSection, requestStation);

            // then
            assertThat(result).isEqualTo(Optional.empty());
        }
    }

    @Nested
    @DisplayName("disconnectSection 메소드")
    class disconnectSection_Method {

        @Test
        @DisplayName("baseSection이 상행 종점 구간이고 상행역이 requestStation과 일치하면 연결을 끊고하고 null을 반환한다")
        void Return_Null_And_Disconnect_When_Input_UP_Station() {
            // given
            Station requestStation = station1;
            Section baseSection = DomainFixture.Section.buildWithStations(requestStation, station2);
            Section section = DomainFixture.Section.buildWithStations(station2, station3);

            baseSection.connectDownSection(section);

            // when
            Section updateSection = SectionDisconnector
                    .findSectionDisconnector(baseSection, requestStation).get()
                    .disconnectSection(baseSection, requestStation);

            // then
            assertThat(updateSection).isNull();
            assertThat(baseSection.getUpSection()).isNull();
        }

        @Test
        @DisplayName("baseSection이 상행 종점 구간이 아니고 상행역이 requestStation과 일치하면 연결을 끊고 재배치한 후, updateSection을 반환한다")
        void Return_UpdateSection_And_Disconnect_And_Rearrange_When_Input_MIDDLE_Station() {
            // given
            Station requestStation = station2;
            Section section = DomainFixture.Section.buildWithStations(station1, requestStation, 1);
            Section baseSection = DomainFixture.Section.buildWithStations(requestStation, station3, 2);
            section.connectDownSection(baseSection);

            // when
            Section updateSection = SectionDisconnector
                    .findSectionDisconnector(baseSection, requestStation).get()
                    .disconnectSection(baseSection, requestStation);

            // then
            assertThat(updateSection).isNotNull();
            assertThat(updateSection.getDistance()).isEqualTo(3);
            assertThat(updateSection.getUpStation()).isEqualTo(station1);
            assertThat(updateSection.getDownStation()).isEqualTo(station3);
        }

        @Test
        @DisplayName("baseSection이 하행 종점 구간이고 하행역이 requestStation과 일치하면 연결을 끊고 null을 반환한다")
        void Return_Null_And_Disconnect_When_Input_DOWN_Station() {
            // given
            Station requestStation = station3;
            Section section = DomainFixture.Section.buildWithStations(station1, station2);
            Section baseSection = DomainFixture.Section.buildWithStations(station2, requestStation);
            section.connectDownSection(baseSection);

            // when
            Section updateSection = SectionDisconnector
                    .findSectionDisconnector(baseSection, requestStation).get()
                    .disconnectSection(baseSection, requestStation);

            // then
            assertThat(updateSection).isNull();
            assertThat(section.getDownSection()).isNull();
        }
    }
}
