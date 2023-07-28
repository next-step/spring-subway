package subway.fixture;

import subway.domain.Station;

public final class StationFixture {

    public static Station 첫번째역() {
        return new Station(1L, "첫번째역");
    }

    public static Station 두번째역() {
        return new Station(2L, "두번째역");
    }

    public static Station 세번째역() {
        return new Station(3L, "세번째역");
    }

    public static Station 네번째역() {
        return new Station(4L, "네번째역");
    }

    public static Station 다섯번째역() {
        return new Station(5L, "다섯번째역");
    }

    public static Station 여섯번째역() {
        return new Station(6L, "여섯번째역");
    }

    public static Station 일곱번째역() {
        return new Station(7L, "일곱번째역");
    }
}
