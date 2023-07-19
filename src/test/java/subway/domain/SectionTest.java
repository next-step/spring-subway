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
    @DisplayName("Builder 메소드는")
    class Builder_Constructor {

        @Test
        @DisplayName("두개의 존재하는 Station이 들어오면, Station이 연결된 Section이 생성된다.")
        void Create_Success_When_Input_Two_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station downStation = new Station(2L, "downStation");
            Integer distance = 10;

            // when
            Throwable throwable = Assertions.catchThrowable(() -> Section.builder()
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build());

            // then
            assertThat(throwable).isNull();
        }

        @Test
        @DisplayName("하나의 Station이 Null값으로 들어오면, IllegalArgumentException을 던진다.")
        void Throw_IllegalArgumentException_When_Input_Null_Station() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station nullStation = null;
            Integer distance = 10;

            // when
            Exception exception = catchException(
                    () -> Section.builder()
                            .upStation(upStation)
                            .downStation(nullStation)
                            .distance(distance)
                            .build());

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("distance로 0 이하의 값이 들어오면, IllegalArgumentException을 던진다.")
        void Throw_IllegalArgumentException_When_Input_Under_Zero() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station downStation = new Station(2L, "downStation");
            Integer zeroDistance = 0;

            // when
            Exception exception = catchException(() -> Section.builder()
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(zeroDistance)
                    .build());

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
            Integer distance = 10;

            Section section = Section.builder()
                    .upStation(upStation)
                    .downStation(middleStation)
                    .distance(distance)
                    .build();

            Section downSection = Section.builder()
                    .upStation(middleStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build();

            // when
            section.connectDownSection(downSection);

            Section result = section.getDownSection();
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
            Station middleStation = new Station(2L, "middleStation");
            Station differentMiddleStation = new Station(3L, "differntMiddleStation");
            Station downStation = new Station(4L, "downStation");
            Integer distance = 10;

            Section section = Section.builder()
                    .upStation(upStation)
                    .downStation(middleStation)
                    .distance(distance)
                    .build();
            Section downSection = Section.builder()
                    .upStation(differentMiddleStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build();

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
            Integer distance = 10;

            Section section = Section.builder()
                    .upStation(upStation)
                    .downStation(middleStation)
                    .distance(distance)
                    .build();

            Section downSection = null;

            // when
            Exception exception = catchException(() -> section.connectDownSection(downSection));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("findDownSection 메소드는")
    class FindDownSection_Method {

        @Test
        @DisplayName("해당 line의 하행 Section을 찾아 반환한다.")
        void Return_Down_Section() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(1L, "middleStation");
            Station downStation = new Station(2L, "downStation");
            Integer distance = 10;

            Section section = Section.builder()
                    .upStation(upStation)
                    .downStation(middleStation)
                    .distance(distance)
                    .build();

            Section downSection = Section.builder()
                    .upStation(middleStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build();

            section.connectDownSection(downSection);

            // when
            Section result = section.findDownSection();

            // then
            assertThat(result).isEqualTo(downSection);
        }
    }

    @Nested
    @DisplayName("disconnectDownSection 메소드는")
    class DisconnectDownSection_Method {

        @Test
        @DisplayName("호출되면, DownSection과 연결을 해제한다")
        void Disconnect_DownSection() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(1L, "middleStation");
            Station downStation = new Station(2L, "downStation");
            Integer distance = 10;

            Section upSection = Section.builder()
                    .upStation(upStation)
                    .downStation(middleStation)
                    .distance(distance)
                    .build();

            Section downSection = Section.builder()
                    .upStation(middleStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build();

            upSection.connectDownSection(downSection);

            // when
            upSection.disconnectDownSection();

            // then
            assertThat(upSection.getDownSection()).isNull();
            assertThat(downSection.getUpSection()).isNull();
        }

        @Test
        @DisplayName("downSection이 null 이라면, IllegalStateException을 던진다")
        void Throw_IllegalStateException_When_Null_DownSection() {
            // given
            Station upStation = new Station(1L, "upStation");
            Station middleStation = new Station(1L, "middleStation");
            Integer distance = 10;

            Section section = Section.builder()
                    .upStation(upStation)
                    .downStation(middleStation)
                    .distance(distance)
                    .build();

            // when
            Exception exception = catchException(section::disconnectDownSection);

            // then
            assertThat(exception).isInstanceOf(IllegalStateException.class);
        }

    }

}
