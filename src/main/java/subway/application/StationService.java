package subway.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

@Service
public class StationService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public StationService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = stationDao.insert(new Station(stationRequest.getName()));
        return StationResponse.of(station);
    }

    public StationResponse findStationResponseById(Long id) {
        return StationResponse.of(stationDao.findById(id));
    }

    public List<StationResponse> findAllStationResponses() {
        List<Station> stations = stationDao.findAll();

        return stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void updateStation(Long id, StationRequest stationRequest) {
        stationDao.update(new Station(id, stationRequest.getName()));
    }

    public void deleteStationById(Long id) {
        stationDao.deleteById(id);
    }

    public List<Station> findStationByLineId(Long lineId) {
        List<Section> sectionList = sectionDao.findAllByLineId(lineId);

        Sections sections = new Sections(sectionList);
//
//        List<Section> result = sort(sections);
//        List<Station> stationsa = new ArrayList<>();
//
//        Stations stations = sections.toStations();
//
//        stations.add(result.get(0).getUpStation());
//        stations.addAll(result.stream().map(Section::getDownStation).collect(Collectors.toList()));

        return sections.toStations();
    }

    private List<Section> sort(List<Section> sections) {
        Section pivot = getFirstSection(sections);
        return getSortedSections(sections, pivot);
    }

    private Section getFirstSection(List<Section> sections) {
        Section pivot = sections.get(0);
        while (true) {
            Optional<Section> temp = findUpSection(sections, pivot);
            if (temp.isEmpty()) {
                return pivot;
            }
            pivot = temp.get();
        }
    }

    private List<Section> getSortedSections(List<Section> sections, Section pivot) {
        List<Section> result = new ArrayList<>();
        while (true) {
            result.add(pivot);
            Optional<Section> temp = findDownSection(sections, pivot);
            if (temp.isEmpty()) {
                return result;
            }
            pivot = temp.get();
        }
    }

    private Optional<Section> findUpSection(List<Section> sections, Section pivot) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(pivot.getUpStationId()))
                .findAny();
    }

    private Optional<Section> findDownSection(List<Section> sections, Section pivot) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(pivot.getDownStationId()))
                .findAny();
    }
}
