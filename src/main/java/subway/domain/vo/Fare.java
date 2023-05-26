package subway.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Objects;

import static subway.domain.vo.Fare.FareTable.*;

@Getter
public class Fare {

    private Integer value;
    private double distance;

    public Fare(Integer fare) {
        this.value = fare;
    }

    public static Fare fromDistance(double distance) {
        return new Fare(calculateFare(distance));
    }

    private static int calculateFare(double distance) {
        if (distance < DEFAULT.limitDistance) {
            return DEFAULT.fare;
        }
        if (distance < MIDDLE.limitDistance) {
            int unit = (int)Math.ceil(((distance - DEFAULT.limitDistance) / (double)MIDDLE.distanceUnit));
            return DEFAULT.fare + unit * MIDDLE.fare;
        }
        int unit = (int)Math.ceil(((distance - MIDDLE.limitDistance) / (double)FAR.distanceUnit));
        return 2050 + unit * FAR.fare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fare fare = (Fare) o;
        return Objects.equals(value, fare.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Fare{" +
                "value=" + value +
                '}';
    }

    /**
     * 기본운임(10㎞ 이내): 기본운임 1,250원
     * 이용 거리 초과 시 추가운임 부과
     * 10km~50km: 5km 까지 마다 100원 추가
     * 50km 초과: 8km 까지 마다 100원 추가
     */
    @RequiredArgsConstructor
    @Getter
    enum FareTable {
        DEFAULT(1, 10, 0,  1250),
        MIDDLE(2, 50, 5, 100),
        FAR(3, Integer.MAX_VALUE, 8, 100);

        private final int seq;
        private final int limitDistance;
        private final int distanceUnit;
        private final int fare;
    }
}
