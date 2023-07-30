package subway.dto;

public class StationRequest {

    private String name;

    public StationRequest() {
    }

    public StationRequest(final String name) {
        this.name = name;
    }

    public boolean hasNullField() {
        return name == null;
    }

    public String getName() {
        return name;
    }
}
