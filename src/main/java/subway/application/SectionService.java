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

            if (distinctStation(sections).contains(downStation)) {
                throw new IllegalArgumentException("이미 노선에 추가되어 있는 역입니다.");
            }
        }
        Section section = sectionDao.insert(line.getId(), new Section(upStation, downStation, request.getDistance()));
        return SectionResponse.of(section);
    }

    private List<Station> distinctStation(List<Section> sections) {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations.stream().distinct().collect(Collectors.toList());
    }

}
