package subway.acceptance.helper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.dto.request.StationCreateRequest;

import static subway.acceptance.helper.RestHelper.post;

public class StationHelper {

    private StationHelper() {
        throw new UnsupportedOperationException();
    }

    public static ExtractableResponse<Response> createStation(final String name) {
        final StationCreateRequest request = new StationCreateRequest(name);

        return post(request, "/stations");
    }
}
