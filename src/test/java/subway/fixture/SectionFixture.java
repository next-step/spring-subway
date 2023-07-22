package subway.fixture;

import subway.dao.SectionDao;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

public class SectionFixture {

    private Section sectionA;
    private Section sectionB;
    private Section sectionC;

    public void init(final SectionDao sectionDao, final LineFixture lineFixture, final StationFixture stationFixture) {
        Line line = lineFixture.getLine();

        Station stationA = stationFixture.getStationA();
        Station stationB = stationFixture.getStationB();
        Station stationC = stationFixture.getStationC();
        Station stationD = stationFixture.getStationD();

        sectionA = sectionDao.insert(new Section(line, stationA, stationB, new Distance(10L)));
        sectionB = sectionDao.insert(new Section(line, stationB, stationC, new Distance(10L)));
        sectionC = sectionDao.insert(new Section(line, stationC, stationD, new Distance(10L)));
    }

    public Section getSectionA() {
        return sectionA;
    }

    public Section getSectionB() {
        return sectionB;
    }

    public Section getSectionC() {
        return sectionC;
    }
}
