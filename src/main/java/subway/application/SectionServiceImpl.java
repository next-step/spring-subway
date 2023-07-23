package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.*;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

import java.util.List;
import java.util.Optional;

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
        Line line = lineDao.findById(lineId).orElseThrow();
        Station upStation = stationDao.findById(request.getUpStationId()).get();
        Station downStation = stationDao.findById(request.getDownStationId()).get();
        Distance distance = new Distance(request.getDistance());
        Section section = new Section(
                line,
                upStation,
                downStation,
                distance);

        preprocessSaveSection(section);

        Section result = sectionDao.insert(section).orElseThrow();
        return SectionResponse.from(result);
    }

    private void preprocessSaveSection(Section section) {
        if (!sectionDao.existByLineId(section.getLineId())) {
            return;
        }

        boolean isUpStationInLine = sectionDao.existByLineIdAndStationId(section.getLineId(),
                section.getUpStationId());
        boolean isDownStationInLine = sectionDao.existByLineIdAndStationId(section.getLineId(),
                section.getDownStationId());

        validateBothExistOrNot(isUpStationInLine, isDownStationInLine);

        updateOriginalSectionIfExist(section, isUpStationInLine, isDownStationInLine);
    }

    private void updateOriginalSectionIfExist(
            Section section,
            boolean isUpStationInLine,
            boolean isDownStationInLine) {
        if (isUpStationInLine) {
            optionalSectionWithUpStation(section).ifPresent(
                    originalSection -> updateOriginalSection(section, originalSection));
        }

        if (isDownStationInLine) {
            optionalSectionWithDownStation(section).ifPresent(
                    originalSection -> updateOriginalSection(section, originalSection));
        }
    }

    private void validateBothExistOrNot(boolean isUpStationInLine, boolean isDownStationInLine) {
        if (!isUpStationInLine && !isDownStationInLine) {
            throw new IllegalArgumentException("추가할 구간의 하행역과 상행역이 기존 노선에 하나는 존재해야합니다.");
        }

        if (isUpStationInLine && isDownStationInLine) {
            throw new IllegalArgumentException("추가할 구간의 하행역과 상행역이 기존 노선에 모두 존재해서는 안됩니다.");
        }
    }

    private Optional<Section> optionalSectionWithUpStation(Section section) {
        return sectionDao.findByLineIdAndUpStationId(
                section.getLineId(),
                section.getUpStationId());
    }

    private Optional<Section> optionalSectionWithDownStation(Section section) {
        return sectionDao.findByLineIdAndDownStationId(
                section.getLineId(),
                section.getDownStationId());
    }

    private void updateOriginalSection(Section section, Section originalSection) {
        Section generatedSection = originalSection.cuttedSection(section);
        sectionDao.update(generatedSection);
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
            throw new IllegalArgumentException("노선에 등록된 하행 종점역만 제거할 수 있습니다.");
        }
    }
}
