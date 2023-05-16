package subway.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.SectionRepository;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.ErrorType;
import subway.exception.ServiceException;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(SectionRepository sectionRepository, StationDao stationDao, LineDao lineDao) {
        this.sectionRepository = sectionRepository;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public SectionResponse createSection(long lineId, SectionRequest request) {
        Line line = findLineById(lineId);
        Station downStation = findStationById(request.getDownStationId());
        Station upStation = findStationById(request.getUpStationId());
        Sections sections = findSectionByLineId(line.getId());

        Section section = Section.builder()
                .lineId(lineId)
                .downStationId(downStation.getId())
                .upStationId(upStation.getId())
                .distance(request.getDistance())
                .build();
        sections.addSection(section);
        section = sectionRepository.save(section);

        return SectionResponse.of(section);
    }


    private Line findLineById(long lineId) {
        Line line = lineDao.findById(lineId);
        if (Objects.isNull(line)) {
            throw new ServiceException(ErrorType.NOT_EXIST_LINE);
        }

        return line;
    }

    public void deleteSection(Long lineId, Long stationId) {
        Sections sections = findSectionByLineId(lineId);
        Station station = findStationById(stationId);
        sections.deleteSection(station);
        sectionRepository.deleteByLineIdAndDownStationId(lineId, stationId);
    }

    private Sections findSectionByLineId(long lineId) {
        return sectionRepository.findAllByLineId(lineId);
    }

    private Station findStationById(long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new ServiceException(ErrorType.NOT_EXIST_LINE));
    }
}