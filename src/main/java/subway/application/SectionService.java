package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    public SectionResponse saveSection(Long lineId, SectionRequest request) {
        validateSaveRequest(lineId, request);

        Station upStation = findStation(request.getUpStationId());
        Station downStation = findStation(request.getDownStationId());

        Section insertedSection = sectionDao.insert(lineId, new Section(upStation, downStation, request.getDistance()));
        return SectionResponse.of(insertedSection);
    }

    private Station findStation(Long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Station 입니다. id + " + stationId));
    }

    private void validateSaveRequest(Long lineId, SectionRequest request) {
        if (isLineEmpty(lineId)) {
            throw new IllegalArgumentException("존재하지 않는 Line 입니다. id = " + lineId);
        }

        List<Section> sections = sectionDao.findAllByLineId(lineId);
        List<Station> distinctStations = Section.distinctStations(sections);
        if (isUpStationDoesNotEqualToLastDownStation(sections, distinctStations, request.getUpStationId())) {
            throw new IllegalArgumentException("새로 등록할 구간의 상행역은 기존 하행 종점역만 가능합니다.");
        }
        if (isStationAlreadyExists(distinctStations, request.getDownStationId())) {
            throw new IllegalArgumentException("기존 구간에 포함된 역은 새로운 하행 종점이 될 수 없습니다.");
        }
    }

    private boolean isLineEmpty(Long lineId) {
        return lineDao.findById(lineId).isEmpty();
    }

    private boolean isUpStationDoesNotEqualToLastDownStation(List<Section> sections, List<Station> distinctStations, Long upStationId) {
        return !(sections.isEmpty() || getLastStation(distinctStations).isIdEquals(upStationId));
    }

    private Station getLastStation(List<Station> stations) {
        return stations.get(stations.size() - 1);
    }

    private boolean isStationAlreadyExists(List<Station> distinctStations, Long downStationId) {
        return distinctStations.stream()
                .anyMatch(station -> station.isIdEquals(downStationId));
    }

    public void deleteSectionById(Long lineId, Long sectionId) {
        validateDeleteRequest(lineId, sectionId);

        sectionDao.deleteById(sectionId);
    }

    private void validateDeleteRequest(Long lineId, Long sectionId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        if (isNotLastSection(sections, sectionId)) {
            throw new IllegalArgumentException("마지막 구간만 삭제가 가능합니다.");
        }
    }

    private boolean isNotLastSection(List<Section> sections, Long sectionId) {
        return !(sections.isEmpty() || getLastSection(sections).isIdEquals(sectionId));
    }

    private Section getLastSection(List<Section> sections) {
        return sections.get(sections.size() - 1);
    }

    public SectionResponse findSectionResponseById(Long id) {
        Section persistSection = findSectionById(id);
        return SectionResponse.of(persistSection);
    }

    private Section findSectionById(Long id) {
        return sectionDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Section 입니다. id = " + id));
    }
}
