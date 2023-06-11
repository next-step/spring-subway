package subway.util;

public class FareCalculator {

    private static final int BASE_FARE = 1250;
    private static final int ADDITIONAL_FARE = 100;
    private static final int FIRST_SURCHARGE_BASE_DISTANCE = 10;
    private static final int SECOND_SURCHARGE_BASE_DISTANCE = 50;
    private static final int FIRST_ADDITIONAL_DISTANCE = 5;
    private static final int SECOND_ADDITIONAL_DISTANCE = 8;

    public static int calculateByDistance(int distance) {
        int fare = BASE_FARE;

        if (distance > SECOND_SURCHARGE_BASE_DISTANCE) {
            int distanceOver50 = distance - SECOND_SURCHARGE_BASE_DISTANCE;
            fare += ((distanceOver50 + SECOND_ADDITIONAL_DISTANCE - 1) / SECOND_ADDITIONAL_DISTANCE) * ADDITIONAL_FARE;
            distance = SECOND_SURCHARGE_BASE_DISTANCE;
        }
        if (distance > FIRST_SURCHARGE_BASE_DISTANCE) {
            int distanceOver10 = distance - FIRST_SURCHARGE_BASE_DISTANCE;
            fare += ((distanceOver10 + FIRST_ADDITIONAL_DISTANCE - 1) / FIRST_ADDITIONAL_DISTANCE) * ADDITIONAL_FARE;
        }

        return fare;
    }
}
