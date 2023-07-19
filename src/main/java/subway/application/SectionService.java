package subway.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

@Service
public class SectionService {

    private static final int MINIMUM_SIZE = 1;
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
        Line line = lineDao.findById(lineId);
        Station upStation = stationDao.findById(request.getUpStationId());
        Station downStation = stationDao.findById(request.getDownStationId());
        Distance distance = new Distance(request.getDistance());
        Section section = new Section(
                line,
                upStation,
                downStation,
                distance);

        preprocessSaveSection(section);

        Section result = sectionDao.insert(section);
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

        if (isUpStationInLine) {
            addSectionWithUpStation(section);
        }

        if (isDownStationInLine) {
            addSectionWithDownStation(section);
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

    private void addSectionWithUpStation(Section section) {
        Optional<Section> optionalSection = sectionDao.findByLineIdAndUpStationId(
                section.getLineId(),
                section.getUpStationId());
        optionalSection.ifPresent(originalSection -> {
            validateDistance(section, originalSection);
            Section generatedSection = new Section(section.getLine(), section.getDownStation(),
                    originalSection.getDownStation(),
                    new Distance(originalSection.getDistance() - section.getDistance()));
            sectionDao.deleteById(originalSection.getId());
            sectionDao.insert(generatedSection);
        });
    }

    private void validateDistance(Section section, Section originalSection) {
        if (section.getDistance() >= originalSection.getDistance()) {
            throw new IllegalArgumentException("역사이에 역 등록시 구간이 기존 구간보다 작아야합니다.");
        }
    }

    private void addSectionWithDownStation(Section section) {
        Optional<Section> optionalSection = sectionDao.findByLineIdAndDownStationId(
                section.getLineId(),
                section.getDownStationId());
        optionalSection.ifPresent(originalSection -> {
            validateDistance(section, originalSection);
            Section generatedSection = new Section(section.getLine(),
                    originalSection.getUpStation(),
                    section.getUpStation(),
                    new Distance(originalSection.getDistance() - section.getDistance()));
            sectionDao.deleteById(originalSection.getId());
            sectionDao.insert(generatedSection);
        });
    }

    public List<Station> findStationByLineId(Long lineId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        if (sections.isEmpty()) {
            return List.of();
        }

        List<Section> result = sort(sections);
        List<Station> stations = new ArrayList<>();
        stations.add(result.get(0).getUpStation());
        stations.addAll(result.stream().map(Section::getDownStation).collect(Collectors.toList()));

        return stations;
    }

    private List<Section> sort(List<Section> sections) {
        Section pivot = getFirstSection(sections);
        return getSortedSections(sections, pivot);
    }

    private Section getFirstSection(List<Section> sections) {
        Section pivot = sections.get(0);
        while (true) {
            Optional<Section> temp = findUpSection(sections, pivot);
            if (temp.isEmpty()) {
                return pivot;
            }
            pivot = temp.get();
        }
    }

    private List<Section> getSortedSections(List<Section> sections, Section pivot) {
        List<Section> result = new ArrayList<>();
        while (true) {
            result.add(pivot);
            Optional<Section> temp = findDownSection(sections, pivot);
            if (temp.isEmpty()) {
                return result;
            }
            pivot = temp.get();
        }
    }

    private Optional<Section> findUpSection(List<Section> sections, Section pivot) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(pivot.getUpStationId()))
                .findAny();
    }

    private Optional<Section> findDownSection(List<Section> sections, Section pivot) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(pivot.getDownStationId()))
                .findAny();
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Section lastSection = findLastSection(lineId, stationId);
        sectionDao.deleteById(lastSection.getId());
    }

    private Section findLastSection(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        validateSize(sections);
        Section lastSection = getLastSection(sections);
        validateIsNotLast(stationId, lastSection);
        return lastSection;
    }

    private static void validateIsNotLast(Long stationId, Section lastSection) {
        if (!lastSection.getDownStationId().equals(stationId)) {
            throw new IllegalArgumentException("노선에 등록된 하행 종점역만 제거할 수 있습니다.");
        }
    }

    private static void validateSize(List<Section> sections) {
        if (sections.size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }
    }

    private Section getLastSection(List<Section> sections) {
        Section pivot = sections.get(0);
        while (true) {
            Optional<Section> temp = findDownSection(sections, pivot);
            if (temp.isEmpty()) {
                return pivot;
            }
            pivot = temp.get();
        }
    }


}
