package subway.domain;

public class Section {

    private Long id;
    private Station upStation;
    private Station downStation;
    private int distance;


    public Section(Long id, Station upStation, Station downStation, int distance) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;

    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }


    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public int getCharge() {
        return calculateCharge();
    }

    public int calculateCharge() {
        int fare = 1250;
        if (distance > 50) {
            int over50 = distance - 50;
            fare += ((over50 + 8 - 1) / 8) * 100;
            distance = 50;
        }

        if (distance > 10) {
            int over10 = distance -10;
            fare += ((over10 + 5 - 1) / 5) * 100;
        }
        return fare;
    }

}
