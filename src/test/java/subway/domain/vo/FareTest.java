package subway.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("운임 로직 테스트")
class FareTest {

    @Test
    @DisplayName("9KM")
    void calculateFare_9km() {
        double distance = 9;

        Fare fare = Fare.fromDistance(distance);

        assertThat(fare).isEqualTo(new Fare(1250));
    }

    @Test
    @DisplayName("12KM")
    void calculateFare_12km() {
        double distance = 12;

        Fare fare = Fare.fromDistance(distance);

        assertThat(fare).isEqualTo(new Fare(1350));
    }

    @Test
    @DisplayName("16KM")
    void calculateFare_16km() {
        double distance = 16;

        Fare fare = Fare.fromDistance(distance);

        assertThat(fare).isEqualTo(new Fare(1450));
    }

    @Test
    @DisplayName("58KM")
    void calculateFare_58km() {
        double distance = 58;

        Fare fare = Fare.fromDistance(distance);

        assertThat(fare).isEqualTo(new Fare(2150));
    }
}
