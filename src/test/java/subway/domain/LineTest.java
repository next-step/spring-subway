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
import java.util.Optional;

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
    @DisplayName("findUpdateSectionWhenDisconnect 메소드는")
    class FindUpdateSectionWhenDisconnect_Method {

        @Test
        @DisplayName("removeStation이 line의 하행역과 일치한다면, Optional.empty를 반환한다")
        void Return_Optional_Empty_When_Remove_Station_Equals_Line_Down_Station() {
            // given
            Station removeStation = station3;
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, removeStation);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            // when
            Optional<Section> updateSection = line.findUpdateSectionWhenDisconnect(removeStation);

            // then
            assertThat(updateSection).isEmpty();
        }

        @Test
        @DisplayName("removeStation이 line의 상행역과 일치한다면, Optional.empty를 반환한다")
        void Return_Optional_Empty_When_Remove_Station_Equals_Line_Up_Station() {
            // given
            Station removeStation = station1;
            Section section1 = DomainFixture.Section.buildWithStations(removeStation, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            // when
            Optional<Section> updateSection = line.findUpdateSectionWhenDisconnect(removeStation);

            // then
            assertThat(updateSection).isEmpty();
        }

        @Test
        @DisplayName("line에 구간이 하나만 있다면, LineException을 던진다")
        void Throw_LineException_When_Line_Size_1() {
            // given
            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);

            Line line = new Line(1L, "line", "red", Arrays.asList(section1));

            // when
            Exception exception = catchException(() -> line.findUpdateSectionWhenDisconnect(station2));

            // then
            assertThat(exception).isInstanceOf(LineException.class);
        }


        @Test
        @DisplayName("removeStation이 line의 중간에 존재한다면, 갱신된 Section을 반환한다")
        void Return_Update_Section_When_Remove_Station_Is_Middle() {
            // given
            Station removeStation = station2;
            Section section1 = DomainFixture.Section.buildWithStations(station1, removeStation, 10);
            Section section2 = DomainFixture.Section.buildWithStations(removeStation, station3, 20);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            // when
            Optional<Section> updateSection = line.findUpdateSectionWhenDisconnect(removeStation);

            // then
            assertThat(updateSection.get().getDistance()).isEqualTo(30);
            assertThat(updateSection.get().getUpStation()).isEqualTo(station1);
            assertThat(updateSection.get().getDownStation()).isEqualTo(station3);
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
            Exception exception = catchException(() -> line.findUpdateSectionWhenDisconnect(requestStation));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }
    }

    @Nested
    @DisplayName("findUpdateSection 메소드는")
    class FindUpdateSection_Method {

        @Test
        @DisplayName("입력으로 들어온 Section의 하행 station과 line의 상행 종점 station이 일치하면, Optional.empty를 반환한다")
        void Return_Optional_Empty_When_Input_Down_Station_Equals_Line_Up_Station() {
            // given
            Station upStation = station1;
            Station downStation = station2;
            Section section2 = DomainFixture.Section.buildWithStations(downStation, station3);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section2)));

            Section requestSection = DomainFixture.Section.buildWithStations(upStation, downStation);

            // when
            Optional<Section> updateSection = line.findUpdateSectionWhenConnect(requestSection);

            // then
            assertThat(updateSection).isEmpty();
        }

        @Test
        @DisplayName("입력으로 들어온 Section의 상행 station과 line의 하행 station이 일치하면, Optional.empty를 반환한다")
        void Return_Optional_Empty_When_Input_Up_Station_Equals_Line_Down_Station() {
            // given
            Station upStation = station2;
            Station downStation = station3;
            Section section1 = DomainFixture.Section.buildWithStations(station1, upStation);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1)));

            Section requestSection = DomainFixture.Section.buildWithStations(upStation, downStation);

            // when
            Optional<Section> updateSection = line.findUpdateSectionWhenConnect(requestSection);

            // then
            assertThat(updateSection).isEmpty();
        }

        @Test
        @DisplayName("입력으로 들어온 Section의 하행 station과 line의 중간 station이 일치하면, 갱신된 Section을 반환한다")
        void Return_Update_Section_When_Input_Down_Station_Equals_Line_Middle_Station() {
            // given
            Station upstation = station2;
            Station downStation = station3;
            Section section1 = DomainFixture.Section.buildWithStations(station1, downStation, 10);
            Section section2 = DomainFixture.Section.buildWithStations(downStation, station4, 10);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            Section requestSection = DomainFixture.Section.buildWithStations(upstation, downStation, 5);

            // when
            Section updateSection = line.findUpdateSectionWhenConnect(requestSection).get();

            // then
            assertThat(updateSection.getUpStation()).isEqualTo(station1);
            assertThat(updateSection.getDownStation()).isEqualTo(upstation);
        }

        @Test
        @DisplayName("입력으로 들어온 Section의 상행 station과 line의 중간 station이 일치하면, 갱신된 Section을 반환한다")
        void Return_Update_Section_When_Input_Up_Station_Equals_Line_Middle_Station() {
            // given
            Station upStation = station2;
            Station downStation = station3;
            Section section1 = DomainFixture.Section.buildWithStations(station1, upStation, 10);
            Section section2 = DomainFixture.Section.buildWithStations(upStation, station4, 10);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section1, section2)));

            Section requestSection = DomainFixture.Section.buildWithStations(upStation, downStation, 5);

            // when
            Section updateSection = line.findUpdateSectionWhenConnect(requestSection).get();

            // then
            assertThat(updateSection.getUpStation()).isEqualTo(downStation);
            assertThat(updateSection.getDownStation()).isEqualTo(station4);
        }

        @Test
        @DisplayName("입력으로 들어온 Section의 상행, 하행 역이 line에 모두 존재할 경우, StationException을 던진다")
        void Throw_StationException_When_UpStation_And_DownStation_All_Exists() {
            // given
            Station upStation = station1;
            Station downStation = station2;
            Section section = DomainFixture.Section.buildWithStations(upStation, downStation, 2);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section)));

            Section requestSection = DomainFixture.Section.buildWithStations(upStation, downStation, 1);

            // when
            Exception exception = catchException(() -> line.findUpdateSectionWhenConnect(requestSection));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }

        @Test
        @DisplayName("입력으로 들어온 Section의 상행, 하행 역이 line에 모두 존재하지 않을 경우, StationException을 던진다")
        void Throw_StationException_When_UpStation_And_DownStation_All_Not_Exists() {
            // given
            Station upStation = station1;
            Station downStation = station2;
            Section section = DomainFixture.Section.buildWithStations(station3, station4, 2);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(section)));

            Section requestSection = DomainFixture.Section.buildWithStations(upStation, downStation, 1);

            // when
            Exception exception = catchException(() -> line.findUpdateSectionWhenConnect(requestSection));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
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
