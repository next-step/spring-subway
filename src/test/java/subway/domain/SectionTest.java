package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.DomainFixture;
import subway.exception.SectionException;
import subway.exception.StationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@DisplayName("Section 클래스")
class SectionTest {

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
    @DisplayName("Builder 메소드는")
    class Builder_Constructor {

        @Test
        @DisplayName("두개의 존재하는 Station이 들어오면, Station이 연결된 Section이 생성된다.")
        void Create_Success_When_Input_Two_Station() {
            // given
            Integer distance = 10;

            // when
            Throwable throwable = Assertions.catchThrowable(() -> Section.builder()
                    .upStation(station1)
                    .downStation(station2)
                    .distance(distance)
                    .build());

            // then
            assertThat(throwable).isNull();
        }

        @Test
        @DisplayName("하나의 Station이 Null값으로 들어오면, StationException을 던진다.")
        void Throw_StaionException_When_Input_Null_Station() {
            // given
            Station nullStation = null;
            Integer distance = 10;

            // when
            Exception exception = catchException(
                    () -> Section.builder()
                            .upStation(station1)
                            .downStation(nullStation)
                            .distance(distance)
                            .build());

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }

        @Test
        @DisplayName("distance로 0 이하의 값이 들어오면, SectionException을 던진다.")
        void Throw_SectionException_When_Input_Under_Zero() {
            // given
            Integer zeroDistance = 0;

            // when
            Exception exception = catchException(() -> Section.builder()
                    .upStation(station1)
                    .downStation(station2)
                    .distance(zeroDistance)
                    .build());

            // then
            assertThat(exception).isInstanceOf(SectionException.class);
        }

        @Test
        @DisplayName("upStation과 downStation이 일치하면 StationException을 던진다.")
        void Throw_StationException_When_Input_Same_Station() {
            // given
            Station sameStation = new Station(1L, "sameStation");
            Integer distance = 10;

            // when
            Exception exception = catchException(() -> Section.builder()
                    .upStation(sameStation)
                    .downStation(sameStation)
                    .distance(distance)
                    .build());

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }
    }

    @Nested
    @DisplayName("connectDownSection 메소드는")
    class ConnectDownSection_Method {

        @Test
        @DisplayName("각 Section의 Middle Station이 동일하면, DownStation이 Section에 하행에 연결된다.")
        void Connect_Down_Section_When_Input_Section() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            // when
            section1.connectDownSection(section2);

            Section resultSection2 = section1.getDownSection();
            Section resultSection1 = resultSection2.getUpSection();

            // then
            assertThat(resultSection2).isEqualTo(section2);
            assertThat(resultSection1).isEqualTo(section1);
        }

        @Test
        @DisplayName("각 Section의 Middle Station이 다르면, SectionException을 던진다.")
        void Throw_SectionException_When_Input_Different_Middle_Station() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station3, station4);

            // when
            Exception exception = catchException(() -> section1.connectDownSection(section2));

            // then
            assertThat(exception).isInstanceOf(SectionException.class);
        }

        @Test
        @DisplayName("Section으로 Null이 들어오면, SectionException을 던진다.")
        void Throw_SectionException_When_Input_Null_Section() {
            // given
            Section section = DomainFixture.Section.buildWithStations(station1, station2);
            Section nullSection = null;

            // when
            Exception exception = catchException(() -> section.connectDownSection(nullSection));

            // then
            assertThat(exception).isInstanceOf(SectionException.class);
        }
    }

    @Nested
    @DisplayName("findDownSection 메소드는")
    class FindDownSection_Method {

        @Test
        @DisplayName("해당 line의 하행 Section을 찾아 반환한다.")
        void Return_Down_Section() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            section1.connectDownSection(section2);

            // when
            Section result = section1.findDownSection();

            // then
            assertThat(result).isEqualTo(section2);
        }
    }

    @Nested
    @DisplayName("findUpdateSectionWhenDisconnect 메소드는")
    class FindUpdateSectionWhenDisconnect_Method {

        @Test
        @DisplayName("removeStation이 null이라면, StationException을 던진다")
        void Throw_Exception_When_Null_Remove_Station() {
            // given
            Station removeStation = null;
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            section1.connectDownSection(section2);

            // when
            Exception exception = catchException(() -> section1.findUpdateSectionWhenDisconnect(removeStation));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }

        @Test
        @DisplayName("removeStation이 line의 상행 종점 Station과 일치한다면, 갱신되는 Section은 없다")
        void Return_Null_When_Remove_Station_Equals_Line_Up_Station() {
            // given
            Station removeStation = station1;
            Section section1 = DomainFixture.Section.buildWithStations(removeStation, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            section1.connectDownSection(section2);

            // when
            Optional<Section> updateSection = section1.findUpdateSectionWhenDisconnect(removeStation);

            // then
            assertThat(updateSection).isEmpty();
        }
    }

    @Nested
    @DisplayName("findUpSection 메소드는")
    class FindUpSection_Method {

        @Test
        @DisplayName("Section이 2개일때, 연결된 상단 종점 Section을 반환한다")
        void Return_Up_Section_When_Two_Section() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            section1.connectDownSection(section2);

            // when
            Section result = section2.findUpSection();

            // then
            assertThat(result).isEqualTo(section1);
        }

        @Test
        @DisplayName("Section이 2개 초과일때, 연결된 상단 종점 Section을 반환한다")
        void Return_Up_Section_When_More_Then_Two_Section() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);
            Section section3 = DomainFixture.Section.buildWithStations(station3, station4);

            section1.connectDownSection(section2);
            section2.connectDownSection(section3);

            // when
            Section result = section3.findUpSection();

            // then
            assertThat(result).isEqualTo(section1);
        }


    }

    @Nested
    @DisplayName("findUpdateSectionWhenConnect 메소드는")
    class FindUpdateSectionWhenConnect_Method {

        @Test
        @DisplayName("중간 위치에 상행 Station을 기준으로 갱신된 Section을 반환한다")
        void Return_Update_Section_By_UpStation_On_Middle() {
            // given
            Station upStation = station2;
            Station downStation = station3;
            Section section1 = DomainFixture.Section.buildWithStations(station1, upStation, 10);
            Section section3 = DomainFixture.Section.buildWithStations(upStation, station4, 10);

            section1.connectDownSection(section3);

            Section section2 = DomainFixture.Section.buildWithStations(upStation, downStation, 9);

            // when
            Optional<Section> updateSection = section1.findUpdateSectionWhenConnect(section2);

            // then
            assertThat(updateSection.get().getUpStation()).isEqualTo(downStation);
            assertThat(updateSection.get().getDownStation()).isEqualTo(station4);
        }

        @Test
        @DisplayName("중간 위치에 하행 Station을 기준으로 갱신된 Section을 반환한다")
        void Return_Update_Section_By_DownStation_On_Middle() {
            // given
            Station upStation = station2;
            Station downStation = station3;
            Section section1 = DomainFixture.Section.buildWithStations(station1, downStation, 10);
            Section section3 = DomainFixture.Section.buildWithStations(downStation, station4, 10);

            section1.connectDownSection(section3);

            Section section2 = DomainFixture.Section.buildWithStations(upStation, downStation, 9);

            // when
            Optional<Section> updateSection = section1.findUpdateSectionWhenConnect(section2);

            // then
            assertThat(updateSection.get().getUpStation()).isEqualTo(station1);
            assertThat(updateSection.get().getDownStation()).isEqualTo(upStation);
        }

        @Test
        @DisplayName("상행 종점에 Section을 추가할 경우, 갱신되는 Section은 없다")
        void Return_Null_By_DownStation_On_Up() {
            // given
            Station downStation = station2;
            Section section1 = DomainFixture.Section.buildWithStations(station1, downStation, 100);
            Section section2 = DomainFixture.Section.buildWithStations(downStation, station3, 10);

            // when
            Optional<Section> updateSection = section2.findUpdateSectionWhenConnect(section1);

            // then
            assertThat(updateSection).isEmpty();
        }

        @Test
        @DisplayName("하행 종점에 Section을 추가할 경우, 갱신되는 Section은 없다")
        void Return_Null_By_UpStation_On_Up() {
            // given
            Station upStation = station2;
            Section section1 = DomainFixture.Section.buildWithStations(station1, upStation, 10);
            Section section2 = DomainFixture.Section.buildWithStations(upStation, station3, 100);

            // when
            Optional<Section> updateSection = section1.findUpdateSectionWhenConnect(section2);

            // then
            assertThat(updateSection).isEmpty();
        }

        @Test
        @DisplayName("Null값이 들어오면, SectionException을 던진다")
        void Throw_SectionException_When_Input_Null_Section() {
            // given
            Section section = DomainFixture.Section.buildWithStations(station1, station2);

            Section nullSection = null;

            // when
            Exception exception = catchException(() -> section.findUpdateSectionWhenConnect(nullSection));

            // then
            assertThat(exception).isInstanceOf(SectionException.class);
        }
    }
}
