package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

            Section upSection = Section.builder()
                    .id(1L)
                    .line(line)
                    .upStation(upStation)
                    .downStation(middleStation)
                    .distance(1)
                    .build();

            Section downSection = Section.builder()
                    .id(1L)
                    .line(line)
                    .upStation(middleStation)
                    .downStation(downStation)
                    .distance(1)
                    .build();

            LineManager lineManager = new LineManager(line, Arrays.asList(upSection));

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

            Section upSection = Section.builder()
                    .id(1L)
                    .line(line)
                    .upStation(upStation)
                    .downStation(middleStation)
                    .distance(1)
                    .build();

            Section existSection = Section.builder()
                    .id(1L)
                    .line(line)
                    .upStation(middleStation)
                    .downStation(existStation)
                    .distance(1)
                    .build();

            LineManager lineManager = new LineManager(line, Arrays.asList(upSection));

            // when
            Exception exception = catchException(() -> lineManager.connectDownSection(existSection));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

    }

}
