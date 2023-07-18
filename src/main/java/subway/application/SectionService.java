package subway.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

@Service
public class SectionService {

    private static final int MINIMUM_SIZE = 1;
    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    @Transactional
    public SectionResponse saveSection(Long lineId, SectionRequest request) {
        preprocessSaveSection(lineId, request);

        Section section = sectionDao.insert(request.toEntity(lineId));
        return SectionResponse.from(section);

    }

    private void preprocessSaveSection(Long lineId, SectionRequest request) {
        if (!sectionDao.existByLineId(lineId)) {
            return;
        }

        boolean isUpStationInLine = sectionDao.existByLineIdAndStationId(lineId,
                request.getUpStationId());
        boolean isDownStationInLine = sectionDao.existByLineIdAndStationId(lineId,
                request.getDownStationId());

        validateBothExistOrNot(isUpStationInLine, isDownStationInLine);

        if (isUpStationInLine) {
            addSectionWithUpStation(lineId, request);
        }

        if (isDownStationInLine) {
            addSectionWithDownStation(lineId, request);
        }

    }

    private void addSectionWithDownStation(Long lineId, SectionRequest request) {
        Optional<Section> originalSection = sectionDao.findByLineIdAndDownStationId(lineId,
                request.getDownStationId());
        originalSection.ifPresent(section -> {
            validateDistance(request, section);
            Section generatedSection = new Section(lineId, section.getUpStationId(),
                    request.getUpStationId(),
                    section.getDistance() - request.getDistance());
            sectionDao.deleteById(section.getId());
            sectionDao.insert(generatedSection);
        });
    }

    private static void validateDistance(SectionRequest request, Section sec) {
        if (sec.getDistance() <= request.getDistance()) {
            throw new IllegalArgumentException("역사이에 역 등록시 구간이 기존 구간보다 작아야합니다.");
        }
    }

    private void addSectionWithUpStation(Long lineId, SectionRequest request) {
        Optional<Section> originalSection = sectionDao.findByLineIdAndUpStationId(lineId,
                request.getUpStationId());
        originalSection.ifPresent(section -> {
            validateDistance(request, section);
            Section generatedSection = new Section(lineId, request.getDownStationId(),
                    section.getDownStationId(),
                    section.getDistance() - request.getDistance());
            sectionDao.deleteById(section.getId());
            sectionDao.insert(generatedSection);
        });
    }

    private static void validateBothExistOrNot(boolean isUpStationInLine,
            boolean isDownStationInLine) {
        if (!isUpStationInLine && !isDownStationInLine) {
            throw new IllegalArgumentException("추가할 구간의 하행역과 상행역이 기존 노선에 하나는 존재해야합니다.");
        }

        if (isUpStationInLine && isDownStationInLine) {
            throw new IllegalArgumentException("추가할 구간의 하행역과 상행역이 기존 노선에 모두 존재해서는 안됩니다.");
        }
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Section lastSection = sectionDao.findLastSection(lineId);
        validateDeleteSection(lineId, stationId, lastSection);
        sectionDao.deleteById(lastSection.getId());
    }

    private void validateDeleteSection(Long lineId, Long stationId, Section lastSection) {
        validateOnlyLastDownStation(stationId, lastSection);
        validateGreaterThanMinimumSize(lineId);
    }

    private void validateGreaterThanMinimumSize(Long lineId) {
        if (sectionDao.findAllByLineId(lineId).size() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }
    }

    private static void validateOnlyLastDownStation(Long stationId, Section lastSection) {
        if (!lastSection.getDownStationId().equals(stationId)) {
            throw new IllegalArgumentException("노선에 등록된 하행 종점역만 제거할 수 있습니다.");
        }
    }

    public List<SectionResponse> findAllByLineId(Long lineId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);

        if (sections.isEmpty()) {
            return List.of();
        }

        List<Section> result = sort(sections);

        return result.stream()
                .map(SectionResponse::from)
                .collect(Collectors.toUnmodifiableList());
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
}
