package subway.dto;

import subway.domain.Line;
import subway.domain.Station;

public class LineWithStations {

    private Line line;
    private Station upStation;
    private Station downStation;

    private LineWithStations() {
        /* no-op */
    }

    public LineWithStations(final Line line, final Station upStation, final Station downStation) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Line getLine() {
        return this.line;
    }

    public Station getUpStation() {
        return this.upStation;
    }

    public Station getDownStation() {
        return this.downStation;
    }
}
