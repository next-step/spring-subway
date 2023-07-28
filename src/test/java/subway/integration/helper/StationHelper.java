package subway.integration.helper;

import subway.dto.StationRequest;

public class StationHelper extends RestHelper {

    private StationHelper() {
        throw new UnsupportedOperationException();
    }

    public static void createStation(final String name) {
        final StationRequest request = new StationRequest(name);

        post(request, "/stations");
    }
}
