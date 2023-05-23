package subway.domain;

import subway.exception.fare.FareMinAgeException;
import subway.exception.fare.FareMinDiscountException;

public class Fare {

    private static final Integer MIN_AGE = 0;
    private static final Integer MIN_DISTANCE = 1;

    private final Long fare;

    public Fare(Long fare) {
        this.fare = fare;
    }

    public Fare(Integer age, Integer distance) {
        validate(age, distance);
        this.fare = calculate(distance, age);
    }

    private void validate(Integer age, Integer distance) {
        if (age < MIN_AGE) {
            throw new FareMinAgeException();
        }

        if (distance < MIN_DISTANCE) {
            throw new FareMinDiscountException();
        }
    }

    private Long calculate(Integer distance, Integer age) {
        FareByAge fareByAge = FareByAge.valueOf(age);

        if (fareByAge.isFree()) {
            return 0L;
        }

        return fareByAge.calculateFare() +
            FareByDistance.calculateFare(distance, fareByAge.calculateAdditionalDistanceFare());
    }

    public Long getFare() {
        return fare;
    }
}
