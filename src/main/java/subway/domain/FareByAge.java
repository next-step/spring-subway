package subway.domain;

import java.util.Arrays;
import java.util.function.Predicate;

public enum FareByAge {

    ADULT(0L, 0, age -> 19 <= age && age < 65),
    YOUTH(350L, 20, age -> 13 <= age && age < 19),
    CHILDREN(350L, 50, age -> 6 <= age && age < 13),
    FREE(0L, 100, age -> age < 6 || 65 <= age);

    private static final Long BASIC_FARE = 1250L;
    private static final Long ADDITIONAL_DISTANCE_FARE = 100L;

    private final Long discountFare;
    private final Integer discountPercent;
    private final Predicate<Integer> ageMatch;

    FareByAge(Long discountFare, Integer discountPercent, Predicate<Integer> ageMatch) {
        this.discountFare = discountFare;
        this.discountPercent = discountPercent;
        this.ageMatch = ageMatch;
    }

    public static FareByAge valueOf(int age) {
        return Arrays.stream(values())
            .filter(fareByAge -> fareByAge.ageMatch.test(age))
            .findFirst()
            .orElse(FREE);
    }

    public Long calculateFare() {
        return (BASIC_FARE - discountFare) - ((BASIC_FARE - discountFare) * discountPercent / 100);
    }

    public Long calculateAdditionalDistanceFare() {
        return ADDITIONAL_DISTANCE_FARE - (ADDITIONAL_DISTANCE_FARE * discountPercent / 100);
    }

    public boolean isFree() {
        return this == FREE;
    }
}
