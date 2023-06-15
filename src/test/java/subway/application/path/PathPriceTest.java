package subway.application.path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 요금 계산 기능")
class PathPriceTest {

    @DisplayName("주어진 경로의 요금을 계산한다")
    @ParameterizedTest
    @CsvSource(value = {"5:1_250", "10:1_250", "11:1_350", "15:1_350", "18:1_450", "50:2_050", "51:2_150", "58:2_150", "59:2_250", "78:2_450"}, delimiter = ':')
    public void calculatePrice(int totalDistance, int expectedPrice) {
        assertThat(PathPrice.calculate(totalDistance)).isEqualTo(expectedPrice);
    }
}
