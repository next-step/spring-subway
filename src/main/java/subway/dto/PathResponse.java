package subway.dto;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stations;
    private int distatnce;
    private int charge;

    public PathResponse(List<StationResponse> stations, int distatnce, int charge) {
        this.stations = stations;
        this.distatnce = distatnce;
        this.charge = charge;
    }


    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistatnce() {
        return distatnce;
    }

    public int getCharge() {
        return charge;
    }
}
