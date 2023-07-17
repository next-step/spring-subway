package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Section 클래스")
class SectionTest {

    @Nested
    @DisplayName("New 생성자는")
    class New_Constructor {

        @Test
        @DisplayName("두개의 존재하는 Station이 들어오면, Station이 연결된 Section이 생성된다.")
        void Create_Success_When_Input_Two_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station downStation = new Station(2L, "downStation");

            // when
            Throwable throwable = Assertions.catchThrowable(() -> new Section(upStation, downStation));

            // then
            assertThat(throwable).isNull();
        }

        @Test
        @DisplayName("하나의 Station이 Null값으로 들어오면, IllegalArgumentException을 던진다.")
        void Throw_IllegalArgumentException_When_Input_Null_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station nullStation = null;

            // when
            Exception exception = catchException(() -> new Section(upStation, nullStation));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("connectDownSection 메소드는")
    class ConnectDownSection_Method {

        @Test
        @DisplayName("각 Section의 Middle Station이 동일하면, UpStation이 Section에 상행에 연결된다.")
        void Connect_Down_Section_When_Input_Section() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(1L, "middleStation");
            Station downStation = new Station(2L, "downStation");

            Section section = new Section(upStation, middleStation);
            Section downSection = new Section(middleStation, downStation);

            // when
            section.connectDownSection(downSection);

            Section result = section.getDownsection();
            Section resultUpSection = result.getUpSection();

            // then
            assertThat(result).isEqualTo(downSection);
            assertThat(resultUpSection).isEqualTo(section);
        }

        @Test
        @DisplayName("각 Section의 Middle Station이 다르면, IllegalArgumentException을 던진다.")
        void Throw_IllegalArgumentException_When_Input_Different_Middle_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(1L, "upStation");
            Station differentMiddleStation = new Station(1L, "upStation");
            Station downStation = new Station(2L, "downStation");

            Section section = new Section(upStation, middleStation);
            Section downSection = new Section(differentMiddleStation, downStation);

            // when
            Exception exception = catchException(() -> section.connectDownSection(downSection));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Section으로 Null이 들어오면, IllegalArgumentException을 던진다.")
        void Throw_IllegalArgumentException_When_Input_Null_Section() {
            // given
            Station upStation = new Station(2L, "upStation");
            Station middleStation = new Station(1L, "middleStation");

            Section section = new Section(upStation, middleStation);
            Section downSection = null;

            // when
            Exception exception = catchException(() -> section.connectDownSection(downSection));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
