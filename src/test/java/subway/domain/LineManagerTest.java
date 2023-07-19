package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.DomainFixture;

@DisplayName("LineManager 클래스")
class LineManagerTest {

    @Nested
    @DisplayName("connectDownSection 메소드는")
    class ConnectDownSection_Method {

        @Test
        @DisplayName("line의 하행과 새로운 section의 상행이 일치하는 section이 들어오면, section이 추가된다")
        void Connect_Section_When_Valid_Section_Input() {
            // given
            Line line = new Line(1L, "line", "red");

            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            LineManager lineManager = new LineManager(line, new ArrayList<>(List.of(upSection)));

            // when
            lineManager.connectDownSection(downSection);

            // then
            assertThat(upSection.getDownSection()).isEqualTo(downSection);
        }

        @Test
        @DisplayName("line에 이미 존재하는 station이 새로운 section의 하행 station으로 입력된다면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_When_Input_Exist_Station() {
            // given
            Line line = new Line(1L, "line", "red");

            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station existStation = upStation;

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section existSection = DomainFixture.Section.buildWithStations(middleStation, existStation);

            LineManager lineManager = new LineManager(line, Arrays.asList(upSection));

            // when
            Exception exception = catchException(() -> lineManager.connectDownSection(existSection));

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
            Line line = new Line(1L, "line", "red");

            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            LineManager lineManager = new LineManager(line, new ArrayList<>(List.of(upSection)));
            lineManager.connectDownSection(downSection);

            // when
            lineManager.disconnectDownSection(middleStation);

            // then
            assertThat(upSection.getDownSection()).isNull();
            assertThat(downSection.getUpSection()).isNull();
        }

        @Test
        @DisplayName("입력된 Station이 line의 하행 Station과 일치 하지 않으면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_When_Not_Equal_Station() {
            // given
            Line line = new Line(1L, "line", "red");

            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(2L, "middleStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            LineManager lineManager = new LineManager(line, new ArrayList<>(List.of(upSection)));
            lineManager.connectDownSection(downSection);

            // when
            Exception exception = catchException(() -> lineManager.disconnectDownSection(upStation));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("line에 구간이 하나만 있다면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_When_Line_Size_1() {
            // given
            Line line = new Line(1L, "line", "red");

            Station upStation = new Station(1L, "upStation");
            Station downStation = new Station(3L, "downStation");

            Section upSection = DomainFixture.Section.buildWithStations(upStation, downStation);

            LineManager lineManager = new LineManager(line, Arrays.asList(upSection));

            // when
            Exception exception = catchException(() -> lineManager.disconnectDownSection(downStation));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
