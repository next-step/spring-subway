package subway.application;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.domain.vo.SectionRegistVo;
import subway.dto.request.SectionRegistRequest;

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
    public void registSection(SectionRegistRequest sectionRegistRequest, Long lineId) {
        Station upStation = stationDao.findById(sectionRegistRequest.getUpStationId());
        Station downStation = stationDao.findById(sectionRegistRequest.getDownStationId());
        Line line = lineDao.findById(lineId);
        Section section = new Section(
            upStation,
            downStation,
            line,
            sectionRegistRequest.getDistance()
        );

        Sections sections = sectionDao.findAllByLineId(lineId);
        SectionRegistVo result = sections.registSection(section);

        sectionDao.insert(result.getAddSection());

        if (result.getUpdateSection().isPresent()) {
            sectionDao.update(result.getUpdateSection().get());
        }
    }

    @Transactional
    public void deleteSection(Long stationId, Long lineId) {
        Sections sections = sectionDao.findAllByLineId(lineId);
        Station deleteStation = stationDao.findById(stationId);
        sections.canDeleteStation(deleteStation);

        Optional<Section> upSection = sections.findUpSection(deleteStation);
        Optional<Section> downSection = sections.findDownSection(deleteStation);
        deleteExistedSection(upSection, downSection);
        connectExistedStations(upSection, downSection);
    }

    private void deleteExistedSection(Optional<Section> upSection, Optional<Section> downSection) {
        if (upSection.isPresent() && downSection.isPresent()) {
            sectionDao.deleteById(upSection.get().getId());
            sectionDao.deleteById(downSection.get().getId());
        }
        if (upSection.isPresent() && downSection.isEmpty()) {
            sectionDao.deleteById(upSection.get().getId());
        }
        if (upSection.isEmpty() && downSection.isPresent()) {
            sectionDao.deleteById(downSection.get().getId());
        }
    }

    private void connectExistedStations(Optional<Section> upSection, Optional<Section> downSection) {
        if (upSection.isPresent() && downSection.isPresent()) {
            Section newSection = upSection.get().combineUpSectionToDownSection(downSection.get());
            sectionDao.insert(newSection);
        }
    }

}
