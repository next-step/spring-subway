package subway.application;

import  org.springframework.stereotype.Service;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import java.util.List;

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
        List<Section> sectionList = sectionDao.findAllByLineId(line.getId());
        Sections sections = new Sections(sectionList);
        sections.addValidate(upStation, downStation);
        Section section = sectionDao.insert(line.getId(), new Section(upStation, downStation, request.getDistance()));
        return SectionResponse.of(section);
    }



    public void delete(Long lineId, Long stationId) {
        lineService.findLineById(lineId);
        Station station = stationService.findStationById(stationId);
        List<Section> sectionList = sectionDao.findAllByLineId(lineId);
        Sections sections = new Sections(sectionList);

        if (!sections.findAllStation().get(sections.getSize()).equals(station)) {
            throw new IllegalArgumentException("노선에 등록된 역 중 하행 종점역만 제거할 수 있습니다.");
        }
        sectionDao.delete(stationId);
    }
}
