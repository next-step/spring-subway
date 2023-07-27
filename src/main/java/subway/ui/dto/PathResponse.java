package subway.ui.dto;

import java.util.ArrayList;
import java.util.List;

public class PathResponse {

    public long getDistance() {
        return 1;
    }

    public List<StationResponse> getStations() {
        return new ArrayList<>();
    }
}
