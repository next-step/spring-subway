package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.domain.exception.NotAllowedAddableSectionException;

@DisplayName("Line 클래스")
class LineTest {

    @Nested
    @DisplayName("addSection 메소드는")
    class AddSection_Method {

        @Test
        @DisplayName("Section을 입력받아, Line에 추가한다")
        void Input_Section_And_Add_Line() {
            // given
            Line line = new Line("line", "red");

            Section section = Section.builder()
                    .id(1L)
                    .upStation(new Station(2L, "upStation"))
                    .downStation(new Station(3L, "downStation"))
                    .distance(10)
                    .build();

            // when
            line.addSection(section);
            List<Section> sectionList = line.getSectionList();

            // then
            assertThat(sectionList).contains(section);
        }

        @Test
        @DisplayName("id가 null인 Section이 입력된다면, IllegalArgumentException을 던진다")
        void Throw_IllegalArgumentException_Input_Null_Id_Section() {
            // given
            Line line = new Line("line", "red");

            Section section = Section.builder()
                    .upStation(new Station(2L, "upStation"))
                    .downStation(new Station(3L, "downStation"))
                    .distance(10)
                    .build();

            // when
            Exception exception = catchException(() -> line.addSection(section));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("입력된 Section의 상행이, Line의 하행과 일치하지 않는다면, NotAllowedAddableSectionException을 던진다")
        void Throw_NotAllowedAddableSectionException_Input_Not_Allowed_Section() {
            // given
            Line line = new Line("line", "red");

            Station differentStation = new Station(10L, "differentStation");

            Section section = Section.builder()
                    .id(1L)
                    .upStation(new Station(2L, "upStation"))
                    .downStation(new Station(3L, "downStation"))
                    .distance(10)
                    .build();

            Section connectedSection = Section.builder()
                    .id(1L)
                    .upStation(differentStation)
                    .downStation(new Station(3L, "downStation"))
                    .distance(10)
                    .build();

            line.addSection(section);

            // when
            Exception result = catchException(() -> line.addSection(connectedSection));

            // then
            assertThat(result).isInstanceOf(NotAllowedAddableSectionException.class);
        }
    }
}
