package subway.application;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.SectionCreateType;
import subway.domain.SectionDeleteType;
import subway.domain.Sections;
import subway.domain.Station;
import subway.domain.vo.SectionDeleteVo;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.LineNotFoundException;
import subway.exception.StationNotFoundException;

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

    @Transactional
    public SectionResponse saveSection(Long lineId, SectionRequest request) {
        Line line = lineDao.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(lineId));
        Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException(request.getUpStationId()));
        Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException(request.getDownStationId()));
        Distance distance = new Distance(request.getDistance());

        Optional<Section> cutSection =
                preProcessSaveSection(lineId, upStation, downStation, distance);
        cutSection.ifPresent(sectionDao::update);

        Section section = new Section(line, upStation, downStation, distance);
        Section result = sectionDao.insert(section);
        return SectionResponse.from(result);
    }

    private Optional<Section> preProcessSaveSection(
            Long lineId,
            Station upStation,
            Station downStation,
            Distance distance) {

        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        SectionCreateType createType = SectionCreateType.of(sections, upStation, downStation);
        return createType.cutSection(sections, upStation, downStation, distance);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        SectionDeleteVo deleteVo = findSectionDeleteVo(stationId, sections);
        deleteVo.getDeleteSections().forEach(section -> sectionDao.deleteById(section.getId()));
        deleteVo.getCombinedSection().ifPresent(sectionDao::insert);
    }

    private static SectionDeleteVo findSectionDeleteVo(Long stationId, Sections sections) {
        SectionDeleteType deleteType = SectionDeleteType.of(sections, stationId);
        Optional<Section> upSection = sections.findByDownStationId(stationId);
        Optional<Section> downSection = sections.findByUpStationId(stationId);

        return deleteType.deleteAndCombineSections(upSection, downSection);
    }

}
