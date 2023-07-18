package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Line 클래스")
class LineTest {

    @Nested
    @DisplayName("connectSection 메소드는")
    class ConnectSection_Method {

        @Test
        @DisplayName("Section을 입력받아, Line에 추가한다")
        void Input_Section_And_Add_Line() {
            // given
            Section section = Section.builder()
                    .id(1L)
                    .upStation(new Station(2L, "upStation"))
                    .downStation(new Station(3L, "downStation"))
                    .distance(10)
                    .build();

            // when
            Line line = new Line("line", "red", section);
            List<Section> sectionList = line.getSections();

            // then
            assertThat(sectionList).contains(section);
        }

        @Test
        @DisplayName("id가 null인 Section이 입력된다면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_Input_Null_Id_Section() {
            // given
            Section section = Section.builder()
                    .upStation(new Station(2L, "upStation"))
                    .downStation(new Station(3L, "downStation"))
                    .distance(10)
                    .build();

            // when
            Exception exception = catchException(() -> new Line("line", "red", section));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("입력된 Section의 상행이, Line의 하행과 일치하지 않는다면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_Input_Not_Allowed_Section() {
            // given
            Station differentStation = new Station(10L, "differentStation");

            Long downStationId = 3L;
            Section section = Section.builder()
                    .id(1L)
                    .upStation(new Station(2L, "upStation"))
                    .downStation(new Station(downStationId, "downStation"))
                    .distance(10)
                    .build();

            Section connectedSection = Section.builder()
                    .id(1L)
                    .upStation(differentStation)
                    .downStation(new Station(3L, "downStation"))
                    .distance(10)
                    .build();

            Line line = new Line("line", "red", section);

            // when
            Exception result = catchException(() -> line.connectSection(downStationId, connectedSection));

            // then
            assertThat(result).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("입력된 Section이 null이면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_Input_Null_Section() {
            // given
            Section section = Section.builder()
                    .id(1L)
                    .upStation(new Station(2L, "upStation"))
                    .downStation(new Station(3L, "downStation"))
                    .distance(10)
                    .build();

            Section connectedSection = null;

            Line line = new Line("line", "red", section);

            // when
            Exception result = catchException(() -> line.connectSection(null, connectedSection));

            // then
            assertThat(result).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("입력된 Section이 유효하면 연결된다")
        void Connect_Input_Section() {
            // given
            Long middleStationId = 3L;
            Station middleStation = new Station(middleStationId, "middleStation");

            Section section = Section.builder()
                    .id(1L)
                    .upStation(new Station(2L, "upStation"))
                    .downStation(middleStation)
                    .distance(10)
                    .build();

            Section connectedSection = Section.builder()
                    .id(2L)
                    .upStation(middleStation)
                    .downStation(new Station(5L, "connecetedDownStation"))
                    .distance(10)
                    .build();

            Line line = new Line("line", "red", section);

            // when
            line.connectSection(middleStationId, connectedSection);
            List<Section> result = line.getSections();

            // then
            assertIsSectionConnected(result, section, connectedSection);
        }

        private void assertIsSectionConnected(List<Section> result, Section upSection, Section downSection) {
            assertThat(result).containsAll(List.of(upSection, downSection));
            assertThat(upSection.getDownSection()).isEqualTo(downSection);
            assertThat(upSection).isEqualTo(downSection.getUpSection());
        }
    }
}
