package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.DomainFixture;
import subway.exception.LineException;
import subway.exception.StationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@DisplayName("Line 클래스")
class LineTest {

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
    @DisplayName("disconnectDownSection 메소드는")
    class DisconnectDownSection_Method {

        @Test
        @DisplayName("line의 하행 Station과 일치하는 Station이 들어오면 연결을 끊는다")
        void Return_True_When_Input_Station() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            // when
            line.disconnectDownSection(station3);

            // then
            assertThat(section1.getDownSection()).isNull();
            assertThat(section2.getUpSection()).isNull();
        }

        @Test
        @DisplayName("입력된 Station이 line의 하행 Station과 일치 하지 않으면, StationException을 던진다")
        void Throw_StationException_When_Not_Equal_Station() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            // when
            Exception exception = catchException(() -> line.disconnectDownSection(station1));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }

        @Test
        @DisplayName("line에 구간이 하나만 있다면, LineException을 던진다")
        void Throw_LineException_When_Line_Size_1() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);

            Line line = new Line(1L, "line", "red", Arrays.asList(section1));

            // when
            Exception exception = catchException(() -> line.disconnectDownSection(station2));

            // then
            assertThat(exception).isInstanceOf(LineException.class);
        }

        @Test
        @DisplayName("line의 중간에 있는 section을 삭제하려고 할 경우, StationException을 던진다")
        void Throw_SectionException_When_Disconnect_Middle_Section() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);
            Section section3 = DomainFixture.Section.buildWithStations(station3, station4);

            section1.connectSection(section2);
            section2.connectSection(section3);

            Line line = new Line(1L, "line", "red", Arrays.asList(section1, section2, section3));

            // when
            Exception exception = catchException(() -> line.disconnectDownSection(station3));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }
    }

    @Nested
    @DisplayName("connectSection 메소드는")
    class ConnectSection_Method {

        @Test
        @DisplayName("입력으로 들어온 Section의 하행 station과 line의 상행 종점 station이 일치하면, 연결을 성공한다")
        void Connect_Success_When_Input_Down_Station_Equals_Line_Up_Station() {
            // given
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section2)));

            Section requestSection = DomainFixture.Section.buildWithStations(station1, station2);

            // when
            Exception exception = catchException(() -> line.connectSection(requestSection));

            // then
            assertThat(exception).isNull();
            assertThat(requestSection.getDownSection()).isEqualTo(section2);
            assertThat(section2.getUpSection()).isEqualTo(requestSection);
        }

        @Test
        @DisplayName("입력으로 들어온 Section의 상행, 하행 역이 line에 모두 존재할 경우, StationException을 던진다")
        void Throw_StationException_When_UpStation_And_DownStation_All_Exists() {
            // given
            Section section = DomainFixture.Section.buildWithStations(station1, station2, 2);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section)));

            Section requestSection = DomainFixture.Section.buildWithStations(station1, station2, 1);

            // when
            Exception exception = catchException(() -> line.connectSection(requestSection));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }

        @Test
        @DisplayName("입력으로 들어온 Section의 상행, 하행 역이 line에 모두 존재하지 않을 경우, StationException을 던진다")
        void Throw_StationException_When_UpStation_And_DownStation_All_Not_Exists() {
            // given
            Section section = DomainFixture.Section.buildWithStations(station1, station2, 2);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section)));

            Section requestSection = DomainFixture.Section.buildWithStations(station3, station4, 1);

            // when
            Exception exception = catchException(() -> line.connectSection(requestSection));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }
    }

    @Nested
    @DisplayName("disconnectSection 메소드는")
    class Disconnect_Method {

        @Test
        @DisplayName("입력으로 들어온 Station이 line에 존재할 경우 연결을 끊고 재배치한다")
        void Disconnect_And_RearrangeWhen_Input_Station_Contains_Line() {
            // given
            Station requestStation = station2;
            Section section1 = DomainFixture.Section.buildWithStations(station1, requestStation, 10);
            Section section2 = DomainFixture.Section.buildWithStations(requestStation, station3, 20);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            // when
            Section updateSection = line.disconnectSection(requestStation);

            // then
            assertThat(updateSection.getDistance()).isEqualTo(30);
            assertThat(updateSection.getUpStation()).isEqualTo(station1);
            assertThat(updateSection.getDownStation()).isEqualTo(station3);
        }

        @Test
        @DisplayName("입력으로 들어온 Station이 line에 존재하지 않을 경우, StationException을 던진다")
        void Throw_StationException_When_Station_Not_Exists() {
            // given
            Station requestStation = station4;
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2, 10);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3, 20);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            // when
            Exception exception = catchException(() -> line.disconnectSection(requestStation));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }

        @Test
        @DisplayName("line에 구간이 하나만 있다면, LineException을 던진다")
        void Throw_LineException_When_Line_Size_1() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);

            Line line = new Line(1L, "line", "red", Arrays.asList(section1));

            // when
            Exception exception = catchException(() -> line.disconnectSection(station1));

            // then
            assertThat(exception).isInstanceOf(LineException.class);
        }
    }

    @Nested
    @DisplayName("getSortedStations 메소드는")
    class GetSortedStations_Method {

        @Test
        @DisplayName("정렬된 line의 상행 종점을 기준으로 정렬된 stations을 반환한다.")
        void Return_Sorted_Stations_Orders_By_Line_UpStations() {
            // given
            Section section = DomainFixture.Section.buildWithStations(station2, station3);
            Section requestSection = DomainFixture.Section.buildWithStations(station1, station2);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(requestSection, section)));

            // when
            List<Station> result = line.getSortedStations();

            // then
            assertThat(result).containsExactly(station1, station2, station3);
        }
    }
}
