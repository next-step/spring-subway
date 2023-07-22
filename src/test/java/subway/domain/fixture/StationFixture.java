package subway.domain.fixture;

import subway.dao.StationDao;
import subway.domain.Station;

public class StationFixture {

    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;

    public void init(StationDao stationDao) {
        stationA = stationDao.insert(new Station("낙성대"));
        stationB = stationDao.insert(new Station("사당"));
        stationC = stationDao.insert(new Station("방배"));
        stationD = stationDao.insert(new Station("서초"));
        stationE = stationDao.insert(new Station("교대"));
    }

    public Station getStationA() {
        return stationA;
    }

    public Station getStationB() {
        return stationB;
    }

    public Station getStationC() {
        return stationC;
    }

    public Station getStationD() {
        return stationD;
    }

    public Station getStationE() {
        return stationE;
    }
}
