package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.domain.response.SectionDisconnectResponse;

@DisplayName("Line 클래스")
class LineTest {

    @Nested
    @DisplayName("connectDownSection 메소드는")
    class ConnectDownSection_Method {

        @Test
        @DisplayName("line의 하행과 새로운 section의 상행이 일치하는 section이 들어오면, section이 추가된다")
        void Connect_Section_When_Valid_Section_Input() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            Line line = new Line("line", "red", new ArrayList<>(List.of(upSection)));

            // when
            line.connectSection(downSection);

            // then
            assertThat(upSection.getDownSection()).isEqualTo(downSection);
        }

        @Test
        @DisplayName("line에 이미 존재하는 station이 새로운 section의 하행 station으로 입력된다면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_When_Input_Exist_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station existStation = upStation;

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section existSection = DomainFixture.Section.buildWithStations(middleStation, existStation);

            Line line = new Line("line", "red", new ArrayList<>(List.of(upSection)));

            // when
            Exception exception = catchException(() -> line.connectSection(existSection));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

    }

    @Nested
    @DisplayName("disconnectDownSection 메소드는")
    class DisconnectDownSection_Method {

        @Test
        @DisplayName("line의 하행 Station과 일치하는 Station이 들어오면 연결을 끊는다")
        void Return_True_When_Input_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            Line line = new Line("line", "red", new ArrayList<>(List.of(upSection)));
            line.connectSection(downSection);

            // when
            line.disconnectDownSection(downStation);

            // then
            assertThat(upSection.getDownSection()).isNull();
            assertThat(downSection.getUpSection()).isNull();
        }

        @Test
        @DisplayName("입력된 Station이 line의 하행 Station과 일치 하지 않으면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_When_Not_Equal_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            Line line = new Line("line", "red", new ArrayList<>(List.of(upSection)));
            line.connectSection(downSection);

            // when
            Exception exception = catchException(() -> line.disconnectDownSection(upStation));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("line에 구간이 하나만 있다면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_When_Line_Size_1() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, downStation);

            Line line = new Line("line", "red", new ArrayList<>(List.of(upSection)));

            // when
            Exception exception = catchException(() -> line.disconnectDownSection(downStation));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("line의 중간에 있는 section을 삭제하려고 할 경우, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_When_Disconnect_Middle_Section() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleUpStation = new Station(2L, "middleUpStation");
            Station middleDownStation = new Station(3L, "middleDownStation");
            Station downStation = new Station(4L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleUpStation);
            Section middleUpSection = DomainFixture.Section.buildWithStations(middleUpStation, middleDownStation);
            Section middleDownSection = DomainFixture.Section.buildWithStations(middleDownStation, downStation);

            upSection.connectSection(middleUpSection);
            middleUpSection.connectSection(middleDownSection);

            Line line = new Line("line", "red", Arrays.asList(upSection, middleUpSection, middleDownSection));

            // when
            Exception exception = catchException(() -> line.disconnectDownSection(middleUpStation));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("connectSection 메소드는")
    class ConnectSection_Method {

        @Test
        @DisplayName("입력으로 들어온 Section의 하행 station과 line의 상행 종점 station이 일치하면, 연결을 성공한다")
        void Connect_Success_When_Input_Down_Station_Equals_Line_Up_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section section = DomainFixture.Section.buildWithStations(middleStation, downStation);

            Line line = new Line("line", "red", new ArrayList<>(List.of(section)));

            Section requestSection = DomainFixture.Section.buildWithStations(upStation, middleStation);

            // when
            Exception exception = catchException(() -> line.connectSection(requestSection));

            // then
            assertThat(exception).isNull();
            assertThat(requestSection.getDownSection()).isEqualTo(section);
            assertThat(section.getUpSection()).isEqualTo(requestSection);
        }

        @Test
        @DisplayName("입력으로 들어온 Section의 상행, 하행 역이 line에 모두 존재할 경우, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_When_UpStation_And_DownStation_All_Exists() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");

            Section section = DomainFixture.Section.buildWithStations(upStation, middleStation, 2);

            Line line = new Line("line", "red", new ArrayList<>(List.of(section)));

            Section requestSection = DomainFixture.Section.buildWithStations(upStation, middleStation, 1);

            // when
            Exception exception = catchException(() -> line.connectSection(requestSection));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("getSortedStations 메소드는")
    class GetSortedStations_Method {

        @Test
        @DisplayName("정렬된 line의 상행 종점을 기준으로 정렬된 stations을 반환한다.")
        void Return_Sorted_Stations_Orders_By_Line_UpStations() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section section = DomainFixture.Section.buildWithStations(middleStation, downStation);
            Section requestSection = DomainFixture.Section.buildWithStations(upStation, middleStation);

            Line line = new Line(1L, "line", "red", new ArrayList<>(List.of(requestSection, section)));

            // when
            List<Station> result = line.getSortedStations();

            // then
            assertThat(result).containsExactly(upStation, middleStation, downStation);
        }
    }

    @Nested
    @DisplayName("disconnectSection 메소드는")
    class DisconnectSection_Method {

        @Test
        @DisplayName("line의 하행 Station과 일치하는 Station이 들어오면 연결을 끊는다")
        void Disconnect_Section_When_Matched_Up_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            Line line = new Line("line", "red", new ArrayList<>(List.of(upSection)));
            line.connectSection(downSection);

            // when
            SectionDisconnectResponse result = line.disconnectSection(downStation);

            // then
            assertThat(upSection.getDownSection()).isNull();
            assertThat(result.getUpdatedSections()).containsExactly(upSection);
            assertThat(result.getDeletedSection()).isEqualTo(downSection);
        }

        @Test
        @DisplayName("line에 구간이 하나만 있다면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_When_Line_Size_1() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, downStation);

            Line line = new Line("line", "red", new ArrayList<>(List.of(upSection)));

            // when
            Exception exception = catchException(() -> line.disconnectSection(downStation));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Line의 상행 Station과 일치하는 Station이 들어오면 연결을 끊는다.")
        void Disconnect_Section_When_Matched_Down_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            Line line = new Line("line", "red", new ArrayList<>(List.of(upSection)));
            line.connectSection(downSection);

            // when
            SectionDisconnectResponse result = line.disconnectSection(upStation);

            // then
            assertThat(downSection.getUpSection()).isNull();
            assertThat(result.getUpdatedSections()).containsExactly(downSection);
            assertThat(result.getDeletedSection()).isEqualTo(upSection);
        }

        @Test
        @DisplayName("Line의 중간 Station과 일치하는 Station이 들어오면 연결을 끊는다.")
        void Disconnect_Section_When_Matched_Middle_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            Line line = new Line("line", "red", new ArrayList<>(List.of(upSection)));
            line.connectSection(downSection);

            // when
            SectionDisconnectResponse result = line.disconnectSection(middleStation);

            // then
            assertThat(upSection.getDownSection()).isNull();
            assertThat(result.getUpdatedSections()).containsExactly(upSection);
            assertThat(result.getDeletedSection()).isEqualTo(downSection);
        }
    }
}
