package subway.application.path;

import java.util.Arrays;

public enum PathPrice {

    DEFAULT(0, 10, 1_250, 0),
    FIRST_STEP(10, 50, 1_250, 5),
    SECOND_STEP(50, Integer.MAX_VALUE, 2_050, 8);

    private final int distanceUnderLimit;
    private final int distanceUpperLimit;
    private final int defaultPrice;
    private final int billingUnit;

    PathPrice(int distanceUnderLimit, int distanceUpperLimit, int defaultPrice, int billingUnit) {
        this.distanceUnderLimit = distanceUnderLimit;
        this.distanceUpperLimit = distanceUpperLimit;
        this.defaultPrice = defaultPrice;
        this.billingUnit = billingUnit;
    }

    public static int calculate(int totalDistance) {
        PathPrice pathPrice = Arrays.stream(values())
                .filter(value -> totalDistance <= value.distanceUpperLimit)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("경로의 전체 거리 값이 잘못되었습니다."));

        return pathPrice.defaultPrice
                + calculateOverPrice(totalDistance - pathPrice.distanceUnderLimit,pathPrice.billingUnit);
    }

    private static int calculateOverPrice(int distance, int billingUnit) {
        if (billingUnit == 0) {
            return 0;
        }
        return 100 * ((distance - 1) / billingUnit + 1);
    }
}
