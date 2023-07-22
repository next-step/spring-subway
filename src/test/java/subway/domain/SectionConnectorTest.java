package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.DomainFixture;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SectionConnector 클래스")
public class SectionConnectorTest {

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
    @DisplayName("findSectionConnector 메소드")
    class FindSectionConnector_Method {

        @Test
        @DisplayName("requestSection의 하행역과 baseSection의 상행 종점역이 일치하면 SectionConnector.UP을 반환한다")
        void Return_UP_When_Input_UP_Section() {
            // given
            Section baseSection = DomainFixture.Section.buildWithStations(station2, station3);
            Section requestSection = DomainFixture.Section.buildWithStations(station1, station2);

            // when
            Optional<SectionConnector> result = SectionConnector.findSectionConnector(baseSection, requestSection);

            // then
            assertThat(result).isEqualTo(Optional.of(SectionConnector.UP));
        }

        @Test
        @DisplayName("requestSection의 상행역과 baseSection의 상행 종점역이 일치하면 SectionConnector.MIDDLE_UP을 반환한다")
        void Return_MIDDLE_UP_When_Input_MIDDLE_UP_Section() {
            // given
            Section baseSection = DomainFixture.Section.buildWithStations(station1, station3);
            Section requestSection = DomainFixture.Section.buildWithStations(station1, station2);

            // when
            Optional<SectionConnector> result = SectionConnector.findSectionConnector(baseSection, requestSection);

            // then
            assertThat(result).isEqualTo(Optional.of(SectionConnector.MIDDLE_UP));
        }

        @Test
        @DisplayName("requestSection의 하행역과 baseSection의 하행 종점역이 일치하면 SectionConnector.MIDDLE_DOWN을 반환한다")
        void Return_MIDDLE_DOWN_When_Input_MIDDLE_DOWN_Section() {
            // given
            Section baseSection = DomainFixture.Section.buildWithStations(station1, station3);
            Section requestSection = DomainFixture.Section.buildWithStations(station2, station3);

            // when
            Optional<SectionConnector> result = SectionConnector.findSectionConnector(baseSection, requestSection);

            // then
            assertThat(result).isEqualTo(Optional.of(SectionConnector.MIDDLE_DOWN));
        }

        @Test
        @DisplayName("requestSection의 상행역과 baseSection의 하행 종점역이 일치하면 SectionConnector.DOWN을 반환한다")
        void Return_DOWN_When_Input_DOWN_Section() {
            // given
            Section baseSection = DomainFixture.Section.buildWithStations(station1, station2);
            Section requestSection = DomainFixture.Section.buildWithStations(station2, station3);

            // when
            Optional<SectionConnector> result = SectionConnector.findSectionConnector(baseSection, requestSection);

            // then
            assertThat(result).isEqualTo(Optional.of(SectionConnector.DOWN));
        }
        @Test
        @DisplayName("일치하는 케이스가 없으면 null을 반환한다.")
        void Return_Null_When_Input_DOWN_Section() {
            // given
            Section requestSection = DomainFixture.Section.buildWithStations(station2, station3);
            Section baseSection = DomainFixture.Section.buildWithStations(station1, station4);

            // when
            Optional<SectionConnector> result = SectionConnector.findSectionConnector(baseSection, requestSection);

            // then
            assertThat(result).isEqualTo(Optional.empty());
        }
    }

    @Nested
    @DisplayName("connectSection 메소드")
    class ConnectSection_Method {

        @Test
        @DisplayName("requestSection의 하행역과 baseSection의 상행 종점역이 일치하면 연결을 성공한다")
        void Connect_Success_When_Input_UP_Section() {
            // given
            Section baseSection = DomainFixture.Section.buildWithStations(2L, station2, station3, 10);
            Section requestSection = DomainFixture.Section.buildWithStations(1L, station1, station2, 10);

            // when
            SectionConnector
                    .findSectionConnector(baseSection, requestSection).get()
                    .connectSection(baseSection, requestSection);

            // then
            assertThat(baseSection.getUpStation()).isEqualTo(requestSection.getDownStation());
        }

        @Test
        @DisplayName("requestSection의 상행역과 baseSection의 상행 종점역이 일치하면 연결을 성공한다")
        void Connect_Success_When_Input_MIDDLE_UP_Section() {
            // given
            Section baseSection = DomainFixture.Section.buildWithStations(2L, station1, station3, 10);
            Section requestSection = DomainFixture.Section.buildWithStations(1L, station1, station2, 5);

            // when
            Section newSection = SectionConnector
                    .findSectionConnector(baseSection, requestSection).get()
                    .connectSection(baseSection, requestSection);

            // then
            assertThat(baseSection.getDownStation()).isEqualTo(newSection.getUpStation());
        }

        @Test
        @DisplayName("requestSection의 하행역과 baseSection의 하행 종점역이 일치하면 연결을 성공한다")
        void Connect_Success_When_Input_MIDDLE_DOWN_Section() {
            // given
            Section baseSection = DomainFixture.Section.buildWithStations(2L, station1, station3, 10);
            Section requestSection = DomainFixture.Section.buildWithStations(1L, station2, station3, 5);

            // when
            Section newSection = SectionConnector
                    .findSectionConnector(baseSection, requestSection)
                    .get()
                    .connectSection(baseSection, requestSection);

            // then
            assertThat(baseSection.getUpStation()).isEqualTo(newSection.getDownStation());
        }

        @Test
        @DisplayName("requestSection의 상행역과 baseSection의 하행 종점역이 일치하면 연결을 성공한다")
        void Connect_Success_When_Input_DOWN_Section() {
            // given
            Section baseSection = DomainFixture.Section.buildWithStations(station1, station2);
            Section requestSection = DomainFixture.Section.buildWithStations(station2, station3);

            // when
            Optional<SectionConnector> result = SectionConnector.findSectionConnector(baseSection, requestSection);

            // then
            assertThat(baseSection.getDownStation()).isEqualTo(requestSection.getUpStation());
        }
    }
}
