package subway.application;

import java.util.List;
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
import subway.dto.request.PathRequest;
import subway.dto.request.SectionRegisterRequest;

import java.util.Optional;
import subway.dto.response.PathResponse;

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
        Section section = makeSection(sectionRegisterRequest, lineId);
        Sections sections = sectionDao.findAllByLineId(lineId);

        sections.validSectionsLine();
        sections.validRegisterSection(section);

        sectionDao.insert(section);
        sections.makeUpdateSection(section).ifPresent(sectionDao::update);
    }

    private Section makeSection(SectionRegisterRequest sectionRegisterRequest, Long lineId) {
        Station upStation = stationDao.findById(sectionRegisterRequest.getUpStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 역을 입력했습니다."));
        Station downStation = stationDao.findById(sectionRegisterRequest.getDownStationId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 역을 입력했습니다."));
        Line line = lineDao.findById(lineId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 호선을 입력했습니다."));

        return new Section(
            upStation,
            downStation,
            line,
            sectionRegisterRequest.getDistance()
        );
    }

    @Transactional
    public void deleteSection(Long stationId, Long lineId) {
        Sections sections = sectionDao.findAllByLineId(lineId);
        Station deleteStation = stationDao.findById(stationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 역을 입력했습니다."));

        sections.validSectionsLine();
        sections.validDeleteStation(deleteStation);

        Optional<Section> sectionByUpStation = sections.findSectionByUpStation(deleteStation);
        Optional<Section> sectionByDownStation = sections.findSectionByDownStation(deleteStation);

        deleteSectionsContainingDeleteStation(sectionByUpStation, sectionByDownStation);

        makeCombineSection(sectionByUpStation, sectionByDownStation);
    }

    private void deleteSectionsContainingDeleteStation(
        Optional<Section> sectionByUpStation,
        Optional<Section> sectionByDownStation
    ) {
        sectionByUpStation.ifPresent(section -> sectionDao.deleteById(section.getId()));
        sectionByDownStation.ifPresent(section -> sectionDao.deleteById(section.getId()));
    }

    private void makeCombineSection(
        Optional<Section> sectionByUpStation,
        Optional<Section> sectionByDownStation
    ) {
        sectionByDownStation.ifPresent(
            newSectionUpStation -> sectionByUpStation.ifPresent(
                newSectionDownStation ->
                    sectionDao.insert(newSectionUpStation.combineSection(newSectionDownStation))
            )
        );
    }

    @Transactional
    public PathResponse findStationToStationDistance(PathRequest pathRequest) {
        Station sourceStation = stationDao.findById(pathRequest.getSource())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 역을 입력했습니다."));
        Station targetStation = stationDao.findById(pathRequest.getTarget())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 역을 입력했습니다."));
        Sections allSections = sectionDao.findAll();

        List<Station> sourceToTargetRoute
            = allSections.findStationToStationRoute(sourceStation, targetStation);
        Distance sourceToTargetDistance
            = allSections.findStationToStationDistance(sourceStation, targetStation);

        return new PathResponse(sourceToTargetRoute, sourceToTargetDistance.getDistance());
    }
}
