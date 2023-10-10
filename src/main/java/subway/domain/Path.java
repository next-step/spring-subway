package subway.domain;

import java.util.List;

public class Path {

    private Sections sections;

    public Path(Sections sections) {
        this.sections = sections;
    }

    public Sections getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.findAllStation();
    }

    public int getTotalDistance() {
        return sections.getTotalDistance();
    }

    public int getCharge(int distance) {
        return sections.calculateCharge(distance);
    }
}
