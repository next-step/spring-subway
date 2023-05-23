package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import subway.exception.FareMinAgeException;
import subway.exception.FareMinDiscountException;

class FareTest {

    @DisplayName("요금을 계산할 때 거리와 나이를 입력하면 요금이 계산된다.")
    @ParameterizedTest
    @CsvSource(value = {"5,15,0", "65,58,0", "19,10,1250", "19,11,1350", "19,16,1450", "19,50,2050",
        "19,51,2150", "19,59,2250", "13,10,720", "13,11,800", "13,16,880", "13,50,1360",
        "13,51,1440", "13,59,1520", "6,10,450", "6,11,500", "6,16,550", "6,50,850",
        "6,51,900", "6,59,950"})
    void getFare(int age, int distance, Long expected) {
        // given

        // when
        Fare fare = new Fare(age, distance);
        Long actual = fare.getFare();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("요금을 계산할 때 거리가 0이하이고, 나이는 0이상이면 에러를 반환한다.")
    @Test
    void getFareFalse() {
        // given
        int age = 0;
        int distance = 0;

        // when

        // then
        assertThrows(FareMinDiscountException.class, () -> new Fare(age, distance));
    }

    @DisplayName("요금을 계산할 때 거리가 1이상이고, 나이는 0미만이면 에러를 반환한다.")
    @Test
    void getFareFalse2() {
        // given
        int age = -1;
        int distance = 1;

        // when

        // then
        assertThrows(FareMinAgeException.class, () -> new Fare(age, distance));
    }
}
