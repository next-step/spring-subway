package subway.domain;

import java.util.Arrays;

public enum FareByDistance {

    BASIC(1, 10, 0),
    SHORT_OVER(11, 50, 5),
    LONG_OVER(51, Integer.MAX_VALUE, 8);

    private final Integer startDistance;
    private final Integer endDistance;
    private final Integer unit;

    FareByDistance(Integer startDistance, Integer endDistance, Integer unit) {
        this.startDistance = startDistance;
        this.endDistance = endDistance;
        this.unit = unit;
    }

    public static Long calculateFare(Integer distance, Long additionalDistanceFare) {
        return Arrays.stream(values())
            .mapToLong(fareByDistance -> fareByDistance.calculate(distance, additionalDistanceFare))
            .sum();
    }

    private Long calculate(int distance, Long additionalDistanceFare) {
        if (this == BASIC) {
            return 0L;
        }

        if (distance < startDistance) {
            return 0L;
        }

        if (distance > endDistance) {
            return calculateAdditionalDistanceFare(endDistance, startDistance, unit, additionalDistanceFare);
        }

        return calculateAdditionalDistanceFare(distance, startDistance, unit, additionalDistanceFare);
    }

    private long calculateAdditionalDistanceFare(Integer distance, int additionalDistance, int unit,
        Long additionalDistanceFare) {
        return ((distance - additionalDistance) / unit + 1) * additionalDistanceFare;
    }
}
