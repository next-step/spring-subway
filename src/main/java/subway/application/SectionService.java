package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.domain.vo.SectionsRegister;
import subway.dto.request.SectionRegisterRequest;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
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
    public void registerSection(SectionRegisterRequest sectionRegisterRequest, Long lineId) {
        Station upStation = stationDao.findById(sectionRegisterRequest.getUpStationId());
        Station downStation = stationDao.findById(sectionRegisterRequest.getDownStationId());
        Line line = lineDao.findById(lineId);
        Section section = new Section(
            upStation,
            downStation,
            line,
            sectionRegisterRequest.getDistance()
        );

        Sections sections = sectionDao.findAllByLineId(lineId);
        SectionsRegister result = sections.registerSection(section);

        sectionDao.insert(result.getAddSection());

        if (result.getUpdateSection().isPresent()) {
            sectionDao.update(result.getUpdateSection().get());
        }
    }

    @Transactional
    public void deleteSection(Long stationId, Long lineId) {
        Sections sections = sectionDao.findAllByLineId(lineId);
        Station deleteStation = stationDao.findById(stationId);
        sections.validDeleteStation();

        Optional<Section> sectionByUpStation = sections.findSectionByUpStation(deleteStation);
        Optional<Section> sectionByDownStation = sections.findSectionByDownStation(deleteStation);

        deleteSectionsContainingDeleteStation(sectionByUpStation, sectionByDownStation);

        makeCombineSection(sectionByUpStation, sectionByDownStation);
    }

    private void deleteSectionsContainingDeleteStation(Optional<Section> sectionByUpStation, Optional<Section> sectionByDownStation) {
        sectionByUpStation.ifPresent(section -> sectionDao.deleteById(section.getId()));
        sectionByDownStation.ifPresent(section -> sectionDao.deleteById(section.getId()));
    }

    private void makeCombineSection(Optional<Section> sectionByUpStation, Optional<Section> sectionByDownStation) {
        if (sectionByDownStation.isPresent() && sectionByUpStation.isPresent()) {
            Section newSectionUpStation = sectionByDownStation.get();
            Section newSectionDownStation = sectionByUpStation.get();
            Section newSection = newSectionUpStation.combineSection(newSectionDownStation);
            sectionDao.insert(newSection);
        }
    }
}
