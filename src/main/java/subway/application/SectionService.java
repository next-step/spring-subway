package subway.application;

import org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineService lineService;
    private final StationService stationService;

    public SectionService(SectionDao sectionDao, LineService lineService, StationService stationService) {
        this.sectionDao = sectionDao;
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public SectionResponse save(Long lineId, SectionRequest request) {
        Line line = lineService.findLineById(lineId);
        Station upStation = stationService.findStationById(request.getUpStationId());
        Station downStation = stationService.findStationById(request.getDownStationId());
        List<Section> sections = sectionDao.findAllByLineId(line.getId());
        if (!sections.isEmpty()) {
            if (!sections.get(sections.size()-1).getDownStation().equals(upStation)) {
                throw new IllegalArgumentException("추가하려는 상행역이 기존의 하행역과 불일치합니다.");
            }

            if (findAllStation(sections).contains(downStation)) {
                throw new IllegalArgumentException("이미 노선에 추가되어 있는 역입니다.");
            }
        }
        Section section = sectionDao.insert(line.getId(), new Section(upStation, downStation, request.getDistance()));
        return SectionResponse.of(section);
    }

    public static List<Station> findAllStation(List<Section> sections) {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations.stream().distinct().collect(Collectors.toList());
    }


    public void delete(Long lineId, Long stationId) {
        lineService.findLineById(lineId);
        Station station = stationService.findStationById(stationId);
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        if (!findAllStation(sections).get(sections.size()).equals(station)) {
            throw new IllegalArgumentException("노선에 등록된 역 중 하행 종점역만 제거할 수 있습니다.");
        }
        sectionDao.delete(stationId);
    }
}
