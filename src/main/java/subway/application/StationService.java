package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.request.StationRequest;
import subway.dto.response.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StationService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public StationService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    @Transactional
    public StationResponse saveStation(final StationRequest stationRequest) {
        final Station station = stationDao.insert(new Station(stationRequest.getName()));
        return StationResponse.of(station);
    }

    @Transactional(readOnly = true)
    public StationResponse findStationResponseById(final Long id) {
        return StationResponse.of(stationDao.findById(id));
    }

    @Transactional(readOnly = true)
    public List<StationResponse> findAllStationResponses() {
        return stationDao.findAll().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStation(final Long id, final StationRequest stationRequest) {
        stationDao.update(new Station(id, stationRequest.getName()));
    }

    @Transactional
    public void deleteStationById(final Long id) {
        stationDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Station> findStationByLineId(final Long lineId) {
        List<Section> sectionList = sectionDao.findAllByLineId(lineId);
        Sections sections = new Sections(sectionList);
        return sections.toStations();
    }


}
