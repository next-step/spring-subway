package subway.dto;

public class StationRequest {

    private String name;

    private StationRequest() {
        // 필드가 하나 일 때, 기본 생성자가 없는 경우 직렬화시 오류 발생
    }

    public StationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
