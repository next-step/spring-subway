package subway.application;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.LineNotFoundException;
import subway.exception.SectionDeleteException;
import subway.exception.StationNotFoundException;

@Service
public class SectionServiceImpl implements SectionService {

    private final SectionDao sectionDao;

    private final LineDao lineDao;

    private final StationDao stationDao;

    public SectionServiceImpl(SectionDao sectionDao, LineDao lineDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @Override
    @Transactional
    public SectionResponse saveSection(Long lineId, SectionRequest request) {
        Line line = lineDao.findById(lineId)
                .orElseThrow(() -> new LineNotFoundException(lineId));
        Station upStation = stationDao.findById(request.getUpStationId())
                .orElseThrow(() -> new StationNotFoundException(request.getUpStationId()));
        Station downStation = stationDao.findById(request.getDownStationId())
                .orElseThrow(() -> new StationNotFoundException(request.getDownStationId()));
        Distance distance = new Distance(request.getDistance());

        preProcessSaveSection(lineId, upStation, downStation, distance);

        Section section = new Section(
                line,
                upStation,
                downStation,
                distance);

        Section result = sectionDao.insert(section);
        return SectionResponse.from(result);
    }

    private void preProcessSaveSection(Long lineId, Station upStation, Station downStation,
            Distance distance) {
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        Optional<Section> cuttedSection = sections.validateAndCutSectionIfExist(
                upStation, downStation, distance);
        cuttedSection.ifPresent(sectionDao::update);
    }

    @Override
    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        List<Section> sectionList = sectionDao.findAllByLineId(lineId);
        Sections sections = new Sections(sectionList);
        Section lastSection = sections.findLastSection();
        validateIsNotLast(stationId, lastSection);
        sectionDao.deleteById(lastSection.getId());
    }

    private static void validateIsNotLast(Long stationId, Section lastSection) {
        if (!lastSection.getDownStationId().equals(stationId)) {
            throw new SectionDeleteException("노선에 등록된 하행 종점역만 제거할 수 있습니다.");
        }
    }
}
