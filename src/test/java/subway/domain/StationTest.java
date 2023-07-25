package subway.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static subway.exception.ErrorCode.INVALID_STATION_NAME;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayException;

public class StationTest {
    @Test
    @DisplayName("이름 null일 때 오류 반환")
    void 이름_null_오류_반환(){
        assertThatCode(()-> new Station(null))
            .isInstanceOf(SubwayException.class)
            .hasMessage(INVALID_STATION_NAME.getMessage());
    }

    @Test
    @DisplayName("이름 빈칸일 때 오류 반환")
    void 이름_빈칸_오류_반환(){
        assertThatCode(()-> new Station(""))
            .isInstanceOf(SubwayException.class)
            .hasMessage(INVALID_STATION_NAME.getMessage());
    }

    @Test
    @DisplayName("이름 빈칸일 때 정상")
    void 이름_빈칸_정상_반환(){
        assertThatCode(()-> new Station("2호선"))
            .doesNotThrowAnyException();
    }
}
