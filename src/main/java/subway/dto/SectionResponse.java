package subway.dto;

import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

public class SectionResponse {

    private Long id;
    private final Line line;
    private final Station upStation;
    private final Station downStation;
    private final int distance;

    private SectionResponse(Section section) {
        this.id = section.getId();
        this.line = section.getLine();
        this.upStation = section.getUpStation();
        this.downStation = section.getDownStation();
        this.distance = section.getDistance();
    }

    public Long getId() {
        return id;
    }

    public static SectionResponse of(Section section) {
        return new SectionResponse(section);
    }
}
