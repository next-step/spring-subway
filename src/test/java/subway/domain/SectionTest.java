package subway.domain;

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
            Assertions.assertThat(throwable).isNull();
        }
    }
}
